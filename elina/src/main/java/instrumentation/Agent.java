package instrumentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import instrumentation.transform.AnnotatedMethodCollector;
import instrumentation.transform.ClassField;
import instrumentation.transform.GenerateTask;
import instrumentation.transform.MethodInfo;
import instrumentation.transform.MethodsModifier;
import instrumentation.transform.OuterClassInfo;



public class Agent 
implements ClassFileTransformer{

	/**
	 * 
	 * 
	 * @param className
	 * @param b
	 * @return
	 */
	private Class<?> loadClass (String className, byte[] b) {
		//override classDefine (as it is protected) and define the class.
		Class<?> clazz = null;
		try {
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			Class<?> cls = Class.forName("java.lang.ClassLoader");
			java.lang.reflect.Method method =
				cls.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });

			// protected method invocaton
			method.setAccessible(true);
			try {
				Object[] args = new Object[] { className, b, 0, b.length};
				clazz = (Class<?>) method.invoke(loader, args);
			} finally {
				method.setAccessible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return clazz;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
	 */
	@SuppressWarnings("unchecked")
	public byte[] transform(ClassLoader loader, String cname, Class<?> classe,
			ProtectionDomain arg3, byte[] bytecode)
	throws IllegalClassFormatException {
		try
		{	
			/*
			 * If Loader == NULL => Loader is Bootstrap Loader. Ignore all classes from bootstrap.
			 * The bootstrap class loader loads the core Java libraries.
			 * Only needed System Class Loader classes.
			 */
			if(loader == null)
				return null;

			ClassReader creader = new ClassReader(bytecode);
			
			//Only classes that extends Service are needed
			if(!creader.getSuperName().equals(Constants.SERVICE) && !creader.getSuperName().equals(Constants.ACTIVE_SERVICE))
				return null;

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			//ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

			//class visitor
			AnnotatedMethodCollector amc = new AnnotatedMethodCollector(cname);
			creader.accept(amc, ClassReader.SKIP_FRAMES);

			//class methods annotated information
			Map<String, MethodInfo> annotatedMethods = amc.getAnnotatedMethods();
			Map<String, ClassField> class_fields = amc.getClassFields();
			int innerClassesCount = amc.getInnerClassesCount();
			//

			if(!annotatedMethods.isEmpty())
			{
				ClassNode cn = new ClassNode();
				creader.accept(cn, ClassReader.SKIP_FRAMES);

				//methods list - method node has method instruction list
				List<MethodNode> methods = cn.methods;

				String className = cname;
				int counter = innerClassesCount + 1;
				Map<String, String> method_task_assoc = new HashMap<String, String>();

				Iterator<Entry<String, MethodInfo>> it = annotatedMethods.entrySet().iterator();

				while(it.hasNext())
				{	
					MethodInfo minfo = it.next().getValue();
					String methodName = minfo.getName();
					String methodDesc = minfo.getDesc();
					String methodSign = minfo.getSignature();
					//...

					if(minfo.getAnnotations().contains(Constants.TASK_ANNOTATION))
					{
						MethodNode toCopyBody = this.getMethodNode(methods, methodName, methodDesc);
						
						@SuppressWarnings("rawtypes")
						Iterator ita = toCopyBody.invisibleAnnotations.iterator();
						while(ita.hasNext()){
							AnnotationNode at = (AnnotationNode)ita.next();
							if(at.desc.equals(Constants.TASK_ANNOTATION))
								ita.remove();
						}
							
						//ESTOU A RETIRAR OS METODOS CLONED
//						MethodNode clone = copy(toCopyBody);
//						clone.name = toCopyBody.name+"__cloned";
//						modifyInvocations(clone, methodName);
//						cn.methods.add(clone);

						String taskName = className+"$"+counter;
						counter ++;
						OuterClassInfo outer = new OuterClassInfo(cn.access, cn.name, cn.signature, methodName, methodDesc, methodSign, null);

						GenerateTask gic = new GenerateTask(taskName, outer, toCopyBody, minfo, class_fields, annotatedMethods);
						byte [] code = gic.createTask();
				
						
												

						loadClass(taskName.replace("/", "."), code);


						
						method_task_assoc.put(methodName + methodDesc, taskName);
						
						
						String[] filename=taskName.split("/");
						createClassFile(code, loader.getResource(className+ ".class"),filename[filename.length-1]);
						
//						createClassFile(code, "inner_"+counter+".class");
					}
				}
				
				MethodsModifier modifier = new MethodsModifier(writer, className, class_fields, annotatedMethods, method_task_assoc);
//				creader.accept(modifier, ClassReader.EXPAND_FRAMES);
				cn.accept(modifier);
				byte [] class_code = writer.toByteArray();
				
				String[] filename=cname.split("/");
				createClassFile(class_code, loader.getResource(className+ ".class"),filename[filename.length-1]);
				
				
//				createClassFile(class_code, "outer.class");
			
				//CheckClassAdapter.verify(new ClassReader(class_code), true, new PrintWriter(System.out));
				return class_code;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}


	/*
	 * Nunca retorná null.
	 */
	private MethodNode getMethodNode(List<MethodNode> methods, String mname, String mdesc)
	{
		Iterator<MethodNode> it = methods.iterator();

		while(it.hasNext())
		{
			MethodNode mn = it.next();
			if(mn.name.equals(mname) && mn.desc.equals(mdesc))
				return mn;
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private MethodNode copy(MethodNode method) {
		@SuppressWarnings("unchecked")
		String[] exceptions = (String[]) method.exceptions.toArray(new String[0]);
		MethodNode result = new MethodNode(method.access, method.name, method.desc, method.signature, exceptions) {
			/**
			 * Label remapping.
			 * Old label -> new label.
			 */
			private final Map<Label, Label> labels = new HashMap<Label, Label>();

			@Override
			protected LabelNode getLabelNode(Label label) {
				Label newLabel = labels.get(label);
				if (newLabel == null) {
					newLabel = new Label();
					labels.put(label, newLabel);
				}

				return super.getLabelNode(newLabel);
			}
		};
		method.accept(result);

		return result;
	}
	
	@SuppressWarnings("unused")
	private void modifyInvocations(MethodNode clone, String oldName) {
		@SuppressWarnings("unchecked")
		Iterator<AbstractInsnNode> it = clone.instructions.iterator();
		
		while(it.hasNext())
		{
			AbstractInsnNode instrNode = it.next();
			
			if(instrNode.getOpcode() == Opcodes.INVOKEVIRTUAL)
			{
				MethodInsnNode mNode = (MethodInsnNode) instrNode;
				if(mNode.name == oldName)
					mNode.name = clone.name;
			}
		}
	}


//	private void createClassFile(byte [] bytecode, String filename)
//	{
//		FileOutputStream fos;
//		try {
//			fos = new FileOutputStream(new File(filename));
//			fos.write(bytecode);
//			fos.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	private String implode(String[] ary, String delim) {
	    String out = "";
	    for(int i=0; i<ary.length; i++) {
	        if(i!=0) { out += delim; }
	        out += ary[i];
	    }
	    return out;
	}
	
	private void createClassFile(byte [] bytecode, URL path,String classname)
	{
		String[] aux = path.getPath().split(File.separator);
		aux=Arrays.copyOf(aux, aux.length-1);
		
		//System.out.println(implode(aux, File.separator).replace("%20"," ")+File.separator+classname+".class");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(implode(aux, File.separator).replace("%20"," ")+File.separator+classname+".class"));
			fos.write(bytecode);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Required method for instrumentation agent.
	public static void premain(String arglist, Instrumentation inst) {
		Agent javaAgent = new Agent();
		inst.addTransformer(javaAgent);
	}
}

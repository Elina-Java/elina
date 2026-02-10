package instrumentation.processor;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import instrumentation.Constants;
import instrumentation.Processor;

/**
 * Base class for the automatic code generation for Elina service related features, such as 
 * 	Remote stubs
 * 	Service pools
 * 	Cluster-level SOMD
 * 
 * @author Joao Saramago
 *
 */
public abstract class ClassGenerator implements ElementVisitor<String, String> {

	/**
	 * Template file for method generation
	 */
	private final String methodTemplate;
	
	/**
	 * The class's identifier suffix
	 */
	private final String sufix;

	/**
	 *  The class' identifier
	 */
	protected String className;
	
	/**
	 * The class' fully qualified name
	 */
	private String fullyQualifiedName = "";
	
	/** 
	 * The superclass' identifier
	 */
	private final String superClass;
	
	/**
	 * The class' package identifier
	 */
	protected String packageName;
	
	
	/**
	 * The class' type parameters
	 */
	private final Set<String> typeParameters = new HashSet<String>();
	
	/**
	 * Is the Java class an interface?
	 */
	private final boolean isInterface;

	
	/**
	 * Has the code generation process been cancelled? 
	 */
	boolean isCancelled;
	
	/**
	 * The messager to report error messages
	 */
	 final Messager messager;

	private String template;

	private List<String> imports = new ArrayList<String>();

	boolean handleInnerClasses = false;

	protected List<String> excludes = new ArrayList<String>(); 
	
	/**
	 * An empty char sequence
	 */
	private static final String EMPTY_CHAR_SEQ = "";
	
	
	/**
	 * Constructor 
	 * 
	 * @param superclass The identifier of the superclass
	 * @param sufix A sufix for the class' identifier
	 * @param messager The error messager
	 */
	public ClassGenerator(String superclass, String sufix, Messager messager) {
		this(superclass, sufix, messager, false, Constants.METHOD_TEMPLATE_FILE);
	}
	
	/**
	 * Constructor
	 * 
	 * @param superclass The identifier of the superclass
	 * @param sufix A sufix for the class' identifier
	 * @param messager The error messager
	 * @param is Interface Is is an interface or not
	 */
	public ClassGenerator(String superclass, String sufix, Messager messager, boolean isInterface) {
		this(superclass, sufix, messager, isInterface, Constants.METHOD_TEMPLATE_FILE);
	}

	/**
	 * Constructor
	 * 
	 * @param superclass The identifier of the superclass
	 * @param sufix A sufix for the class' identifier
	 * @param messager The error messager
	 * @param is Interface Is is an interface or not
	 * @param methodTemplateFile The identifier of the method template file
	 */
	public ClassGenerator(String superclass, String sufix, Messager messager, boolean isInterface, String methodTemplateFile) {
		this.superClass = superclass;
		this.sufix = sufix;
		this.messager = messager;
		this.isCancelled = false;
		this.isInterface = isInterface;
		this.methodTemplate = readTemplate(methodTemplateFile);		
	}

	/**
	 * Read a given template file and return its contents as a String
	 * 
	 * @param templateFile The name of the template file
	 * @return The contents of the file as a string
	 */
	static String readTemplate(String templateFile) {

		Scanner s = new Scanner(ClassGenerator.class.getResourceAsStream(templateFile));
		String result = s.useDelimiter("\\A").next();
		s.close();
		return result;
	}
	
	/**
	 * Returns the list of type parameters for the class' superclass
	 * 
	 * @return The list of type parameters
	 */
	protected List<String> getSuperClassTypeParameters() {
		return null;
	}
	
	/**
	 * Add a class type parameter
	 * 
	 * @param classContent Current contents of the class
	 * @param typeElement A type element
	 * 
	 * @return The updated contents of the class
	 */
	private String addClassTypeParameters(String classContent, TypeElement typeElement) {
		if (typeElement.getTypeParameters().size() > 0){
			String types = "";
			for (TypeParameterElement type : typeElement.getTypeParameters()) {
				types += type.getSimpleName() + ", ";
				typeParameters.add(type.getSimpleName().toString());
			}
			types=types.substring(0,types.length()-2);
			classContent = classContent.replace("<type_parameters>", "<"+types+">");
		}
		else
			classContent = classContent.replace("<type_parameters>", EMPTY_CHAR_SEQ);
		return classContent;
	}

	/**
	 * Add a method according to the existing method template
	 * 
	 * @param classContent Current contents of the class
	 * @param e The
	 * 
	 * @return The updated contents of the class
	 */
	protected String addMethods(String classContent, ExecutableElement e) {
		return this.addMethods(methodTemplate, classContent, e);
	}
	
	protected String addMethods(String method, String classContent, ExecutableElement e) {
		if(e.getSimpleName().contentEquals("<init>"))
			return classContent;		
		
		boolean isStatic = false;
		
		if(e.getModifiers().size() > 0){
			String modifiers="";
			for (Modifier m : e.getModifiers()) {
				if(m.equals(Modifier.STATIC)||m.equals(Modifier.PRIVATE)){
					isStatic=true;
					break;
				}
				if(!m.equals(Modifier.ABSTRACT))
					modifiers+=m.toString()+" ";
			}
			if (isStatic)
				return classContent;
			
			modifiers = modifiers.substring(0,modifiers.length()-1);
			method = method.replace("<modifiers>", modifiers);
		}
		else 
			method=method.replace("<modifiers>", EMPTY_CHAR_SEQ);
					
		method = addMethodReturnType(e, method);
		method = method.replace("<name>", e.getSimpleName());
		method = addMethodParameters(e, method);
		method = addMethodThrows(e, method);
		method = addMethodTypeParameters(e, method);
	
		classContent = classContent.replace("<method>", method+"\n<method>");
		
		return classContent;
	}

	protected String addMethodTypeParameters(ExecutableElement e, String method) {
		if(e.getTypeParameters().size()>0){
			String types="";
			for (TypeParameterElement type : e.getTypeParameters()) {
				types+=type.getSimpleName()+", ";
				typeParameters.add(type.getSimpleName().toString());
			}
			types=types.substring(0,types.length()-2);
			method=method.replace("<type_parameters>", "<"+types+">");
		}else{
			method=method.replace("<type_parameters>", EMPTY_CHAR_SEQ);
		}
		return method;
	}

	protected abstract String getConstructor(String name);

	protected String getNewFields() { 
		return "";
	}
	
	protected String addMethodThrows(ExecutableElement e, String method) {
		if(e.getThrownTypes().size()>0){
			String thro="";
			for (TypeMirror thrown : e.getThrownTypes()) {
				thro+=thrown.toString()+", ";
			}
			thro=thro.substring(0,thro.length()-2);
			method=method.replace("<throws>", "throws "+thro);
		}else{
			method=method.replace("<throws>", EMPTY_CHAR_SEQ);
		}
		return method;
	}

	protected String addMethodParameters(ExecutableElement e, String method) {
		if(e.getParameters().size()>0){
			String para = "";
			String var = "";
			String types = "";
			
			List<TypeParameterElement> MethodTypeParameters = (List<TypeParameterElement>) e.getTypeParameters();
			for (VariableElement p : e.getParameters()) {
				para += p.asType().toString()+" "+p.getSimpleName()+", ";
				var += p.getSimpleName()+", ";
				String type = p.asType().toString().replaceAll("<.*>", "");
				
				// Handling type parameters
				boolean typeParameterFound = false;
				if(typeParameters.contains(type))
					typeParameterFound = true;
				for (TypeParameterElement tp : MethodTypeParameters)
					if (type.equals(tp.asType().toString().replaceAll("<.*>", ""))) {
						typeParameterFound = true;
						break;
					}
				
				if (typeParameterFound) {
					types += "Object.class, ";
					continue;
				}
				else {
				// Handling builtin types
					if (type.equals("boolean"))
						type="Boolean";
					else if (type.equals("char"))
						type="Character";
					else if (type.equals("byte"))	
						type="Byte";
					else if (type.equals("short"))
						type="Short";
					else if (type.equals("int"))
						type="Integer";
					else if (type.equals("long"))
						type="Long";
					else if (type.equals("float"))	
						type="Float";
					else if (type.equals("double"))
						type="Double";
					else if (type.equals("void"))
						type="Void";
				
				// 	Handling compound types
					types += type+".class, ";
				}
				
			}
			
			para=para.substring(0,para.length()-2);
			var=var.substring(0,var.length()-2);
			types=types.substring(0,types.length()-2);
			
			method=method.replace("<params>", para);
			method=method.replace("<params_vars>", var);
			
			
			
			
			method=method.replace("<params_types>", types);
		}else{
			method=method.replace("<params>", EMPTY_CHAR_SEQ);
			method=method.replace("<params_vars>", EMPTY_CHAR_SEQ);
			method=method.replace("<params_types>", EMPTY_CHAR_SEQ);
			
		}
		return method;
	}

	protected String addMethodReturnType(ExecutableElement e, String method) {
		method=method.replace("<return_type>", e.getReturnType().toString());
		if(e.getReturnType().getKind().isPrimitive() || e.getReturnType().getKind().equals(TypeKind.VOID)){
			
			if(e.getReturnType().getKind().equals(TypeKind.INT)){
				method=method.replace("<Return_type>","Integer");
			}else{
				method=method.replace("<Return_type>", e.getReturnType().toString().substring(0,1).toUpperCase()+e.getReturnType().toString().substring(1,+e.getReturnType().toString().length()));
			}
		}else{
			method=method.replace("<Return_type>", e.getReturnType().toString());
		}
		if(e.getReturnType().toString().equals("void")){
			method=method.replace("<return>", "aux.get();");
		}else{
			method=method.replace("<return>", "return aux.get();");
		}
		
		
		
		
		switch (e.getReturnType().getKind()) {
			case BOOLEAN:
				method=method.replace("<return_default>", "return false;");
				break;
			case VOID:
				method=method.replace("<return_default>", EMPTY_CHAR_SEQ);
				break;
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
				method=method.replace("<return_default>", "return 0;");
			default:
				method=method.replace("<return_default>", "return null;");
				break;
		}
		
		return method;
	}
	
	private String addImports() {
		if (this.imports.size() == 0) 
			return EMPTY_CHAR_SEQ;
		
		String importCode = "";
		for (String i : this.imports)
			importCode += "import "+ i.toString().replaceFirst("<.*>","")+";\n";
		return  importCode ;
			
	}

	private String addInterfaces(String classContent, TypeElement el) {
		if (el.getInterfaces().size() > 0) {
			String interfaces = "implements ";
			
			for (TypeMirror t : el.getInterfaces()) {
				String itf = t.toString();
				if (notInExcludes(itf)) {
					interfaces += itf + ", ";
					if(!this.packageName.equals("") && itf.contains(this.packageName+"."))
						this.imports.add(itf);
				}
			}
			
			if (isInterface)
				interfaces += this.className + ", ";
			
			interfaces = interfaces.substring(0,interfaces.length()-2);
			
			classContent=classContent.replace("<interfaces>", interfaces);
		}
		else 
			classContent=classContent.replace("<interfaces>", EMPTY_CHAR_SEQ);

		return classContent;
	}

	private boolean notInExcludes(String itf) {
		return !(excludes.contains(itf));
	}

	private String addPackage(String classContent,PackageElement pack) {
		if(!pack.isUnnamed()){
			classContent=classContent.replace("<package>", "package "+pack.getQualifiedName()+";");
			this.packageName = pack.getQualifiedName().toString();
		}else{
			classContent=classContent.replace("<package>", EMPTY_CHAR_SEQ);
			this.packageName = "";
		}
		return classContent;
	}

	
	private String addInnerClass(TypeElement e, String code) {		
		String className = computeClassName((TypeElement) e);	
		String generatedCode = generateTypeBody(e, this.template, generateSimpleClassName(e), className, true);
		
		return code.replace("<innerClass>", generatedCode + "\n <innerClass>");
	}
	
	/**
	 *
	 * @see javax.lang.model.element.ElementVisitor#visit(javax.lang.model.element.Element, java.lang.Object)
	 */
	@Override
	public String visit(Element e, String p) {
		List<? extends Element> child = e.getEnclosedElements();

		for (Element c : child) 
			p = c.accept(this, p);
		
		return this.visit(e);
	}

	@Override
	public String visit(Element e) {
		return "";
	}

	@Override
	public String visitPackage(PackageElement e, String p) {
		List<? extends Element> child = e.getEnclosedElements();
		
		for (Element c : child) 
			p = c.accept(this, p);
		
		return p;
	}
	
	//FIXME CHANGED!!
	public static String debug = "" ;

	@Override
	public String visitType(TypeElement e, String p) {
		
		String name = generateSimpleClassName(e);
		
		Element cur = e ;
		while (!((cur=cur.getEnclosingElement()) instanceof PackageElement))
			;
		PackageElement pack = (PackageElement) cur;
		
		// FIXME
		if (fullyQualifiedName.equals(""))
			fullyQualifiedName = pack.isUnnamed() ? name : pack.getQualifiedName() + "." + name;
	
		p = addPackage(p, pack);
		
		this.className = computeClassName(e);		
		return generateTypeBody(e, p, name, this.className, false);
	}
	
	private String computeClassName (TypeElement e) {
		String className = e.getSimpleName().toString();
		
		if(e.getTypeParameters().size() > 0){
			className += "<";
			for (TypeParameterElement t : e.getTypeParameters()) 
				className+=t.toString()+", ";
			
			className = className.substring(0, className.length()-2);
			className += ">";
		}
		
		return className;
	}
	private String generateTypeBody(TypeElement e, String p, String generatedClassName, String className, boolean innerClass) {
		//FIXME CHANGED !!!
		try {
			debug += 
					"\n##############################\ne="+e + 
					"\n e.getKind()=" + e.getKind()+
					"\n e.getEnclosingElement()=" + e.getEnclosingElement() + 
					"\n e.getEnclosingElement().getKind()=" + e.getEnclosingElement().getKind()+
					"\n fullname="+ fullyQualifiedName +
					"\n this.name="+ className +
					"\n name=" + generatedClassName + "<";
					for (String interf : this.getSuperClassTypeParameters())
						debug += interf + "," ;
					debug += ">" +	
					"\n\n\n";
			File f = new File("LOG_MIDDLEWARE.txt") ;
			FileWriter fout = new FileWriter(f);
			fout.write(debug);
			fout.flush();
			fout.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	
		// Check if original class is abstract and, if so, apply that same modifier to the generated class
		boolean isAbstract = false;
		for (Modifier m : e.getModifiers()) {
			if (m.equals(Modifier.ABSTRACT) && e.getKind()==ElementKind.CLASS)
				isAbstract = true;
		}
		p = p.replace("<abstract>", isAbstract ? "abstract" : EMPTY_CHAR_SEQ); 
		
		// Class name
		p = p.replace("<class_name>", generatedClassName);
		
		// Define the superclass as being the one of the orignal class, if a service,  
		System.out.println("class " + e);
		TypeElement superClassElement = Processor.asElement(e.getSuperclass());
		System.out.println("superclass " + e.getSuperclass());
		System.out.println("superclass element " + superClassElement);
		System.out.println("template superclass element " + getSuperClass());
		boolean isSuperClassAService = superClassElement != null && 
				!(superClassElement.toString().equals(Constants.SERVICE)) &&
				!(superClassElement.toString().equals(Constants.ZIB_SERVICE)) &&
				!(superClassElement.toString().equals(this.superClass)) &&
				Processor.isService(superClassElement);
		
		System.out.println("superclass isService " + isSuperClassAService);
		p = p.replace("<super_class>",  isSuperClassAService ? 
				generateSuperClassName(e.getSuperclass(), innerClass) : 
				getSuperClass());			
		
		
		p = p.replace("<fields>", getNewFields());
		p = p.replace("<constructor>", getConstructor(generatedClassName));
		p = addClassTypeParameters(p, e);
		
		List<? extends Element> child = e.getEnclosedElements();
		for (Element c : child) {
			// FIXME
			debug += "\n\n==================== child:" + c.getSimpleName()+ "\n " + c.getKind();
		
			if (c.getKind() == javax.lang.model.element.ElementKind.CLASS ||
					c.getKind() == javax.lang.model.element.ElementKind.INTERFACE) {
				if (this.handleInnerClasses && Processor.isService((TypeElement) c))
					p = addInnerClass((TypeElement) c, p);
			}
			else 
				p = c.accept(this, p);
		}
		return addInterfaces(p,e); 
	}

	
	private String getSuperClass() {
		String generatedSuperClass = this.superClass;
		List<String> superClassTypeParameters = getSuperClassTypeParameters();
		if (superClassTypeParameters != null && superClassTypeParameters.size() > 0) {
			generatedSuperClass += "<" + superClassTypeParameters.get(0);	 
			for (int i = 1; i < superClassTypeParameters.size(); i++)
				generatedSuperClass += "," + superClassTypeParameters.get(i);
			generatedSuperClass += ">";
		}
		return generatedSuperClass;
	}

	@Override
	public String visitVariable(VariableElement e, String p) {

		List<? extends Element> child = e.getEnclosedElements();
		for (Element c : child) 
			p=c.accept(this, p);
		
		return p;
	}

	@Override
	public String visitExecutable(ExecutableElement e, String p) {
		List<? extends Element> child = e.getEnclosedElements();

		for (Element c : child) 
			p = c.accept(this, p);
		
		return addMethods(p,e);
	}

	@Override
	public String visitTypeParameter(TypeParameterElement e, String p) {
		List<? extends Element> child = e.getEnclosedElements();

		for (Element c : child) 
			p=c.accept(this, p);
		
		return p;
	}

	@Override
	public String visitUnknown(Element e, String p) {
		List<? extends Element> child = e.getEnclosedElements();

		for (Element c : child) 
			p=c.accept(this, p);
		
		return p;
	}
	
	public String getFullClassName() {
		return this.fullyQualifiedName;
	}

	public boolean isCancelled() {
		return this.isCancelled;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String finalize(String code) {
			code = code.replaceFirst("<imports>", addImports());
			code = code.replaceAll("<imports>", "");
			code = code.replace("<method>", "");
			code = code.replace("<package>", "");
			code = code.replace("<innerClass>", "");
			code = code.replaceAll("  ", " ");
			code = code.trim();
			return code;
	}
	
	private String generateSimpleClassName(TypeElement clazz) {
		return clazz.getSimpleName() + this.sufix;
	}

	private String generateSuperClassName(TypeMirror clazz,  boolean inner) {
		String name = Processor.asElement(clazz).getQualifiedName().toString();  
		
		if (inner) {
			int index = name.lastIndexOf('.');
			String className = name.substring(index+1);
			String outerClassName = name.substring(0,index);
			name =  outerClassName + this.sufix + "." + className + this.sufix;
		}
		else
			name += this.sufix;
		
		int index = clazz.toString().lastIndexOf('<');
		if (index == -1)
			return name;
		
		return name + clazz.toString().substring(index);
		
	}
	
}

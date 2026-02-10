package instrumentation.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

import instrumentation.Constants;
import instrumentation.definitions.DistributionPolicy;
import instrumentation.definitions.ReductionPolicy;

public class DistRedGenerator extends ClassGenerator {

	private final String DISTRED_METHOD_TEMPLATE;
	private final Trees treeUtils;
	private final Filer filer;
	private final Set<String> imports = new HashSet<String>();
	private final Set<String> distRedMethods = new HashSet<String>();
	
	public DistRedGenerator(Messager messager, Trees treeUtils, Filer filer) {
		super(Constants.DIST_RED_SERVICE, "DistRed", messager); 
		DISTRED_METHOD_TEMPLATE = readTemplate(Constants.DISTRED_METHOD_TEMPLATE_FILE);
		this.treeUtils = treeUtils;
		this.filer = filer;
		this.excludes.add(Constants.IRECONFIGURABLE);
	}

	
	@Override
	public String visitType(TypeElement e, String p) {
		if (e.getKind().equals(ElementKind.INTERFACE))
			this.isCancelled = true;
		else {
			for (TypeMirror t : e.getInterfaces()) {
				if (DistRedVisitor.distRedTypes.containsKey(t.toString())) {
					distRedMethods.addAll(DistRedVisitor.distRedTypes.get(
							t.toString()).accept(new DistRedInterfaceVisitor(),
							null));
				}
			}
		}

		if (treeUtils != null) {
			TreePath tp = treeUtils.getPath(e);
			if (tp != null) {
				tp.getCompilationUnit().accept(new TreeScanner<Void, Void>() {
					@Override
					public Void visitImport(ImportTree node, Void p) {
						super.visitImport(node, p);

						imports.add(node.toString());

						return null;
					}

				}, null);
			}
		}

		return super.visitType(e, p);
	}

	@Override
	protected String getConstructor(String name) {
		return "public " + name
				+ "(" + Constants.SERVICE_INTERFACE + "[] services) " +
						"{\n\t\tsuper(services);\n\t}";
	}

	@Override
	protected String addMethods(String classContent, ExecutableElement e) {

		if (e.getSimpleName().toString().equals("init"))
			return classContent;

		String distribution;
		String reduction;
		String[] distributionParams;
		String[] reductionParams;

		DistributionPolicy dist = null;// =e.getAnnotation(Distribution.class);
		List<String> distVars = new ArrayList<String>();
		// String distVarType = null;

		Map<String, String> params = new TreeMap<String, String>();

		for (VariableElement var : e.getParameters()) {
			DistributionPolicy distribution_policy = var.getAnnotation(DistributionPolicy.class);
	
			if (distribution_policy != null) {
				dist = distribution_policy;
				distVars.add(var.getSimpleName().toString());
				// distVarType = var.asType().toString();
			} 
			else 
				params.put(var.getSimpleName().toString(), var.asType().toString());
			
		}

		// String value = "";
		// String key = "";
		// try {
		// value=anno.value().getCanonicalName();
		// } catch (MirroredTypeException mte) {
		// value = mte.getTypeMirror().toString();
		// }
		// try {
		// key=anno.key().getCanonicalName();
		// } catch (MirroredTypeException mte) {
		// key = mte.getTypeMirror().toString();
		// }

		ReductionPolicy red = e.getAnnotation(ReductionPolicy.class);

		if (dist == null || red == null) {
			Set<Modifier> modifiers = e.getModifiers();

			boolean aux = !modifiers.contains(Modifier.STATIC);
			aux = aux && !modifiers.contains(Modifier.ABSTRACT);
			aux = aux
					&& !e.getKind().equals(
							javax.lang.model.element.ElementKind.CONSTRUCTOR);
			aux = aux
					&& this.distRedMethods.contains(e.getSimpleName()
							.toString());

			if (aux)
				messager.printMessage(
						Kind.WARNING,
						"This method doesn't have a Distribution or a Reduction policy",
						e);
			return super.addMethods(classContent, e);
		} else {
			try {
				distribution = dist.distribution().getCanonicalName();
			} catch (MirroredTypeException mte) {
				distribution = mte.getTypeMirror().toString();
			}
			try {
				reduction = red.reduction().getCanonicalName();
			} catch (MirroredTypeException mte) {
				reduction = mte.getTypeMirror().toString();
			}
			
			distributionParams = dist.params();
			reductionParams = red.params();

			classContent = super.addMethods(DISTRED_METHOD_TEMPLATE, classContent, e);

			classContent = classContent.replace("<distribution>", distribution
					+ "(" + this.implode(distributionParams, ", ") + ")");

			
			String dist_type = DistributionsReductionsVisitor.distributions.get(distribution);			
			Pattern p = Pattern.compile("^(core\\.collective\\.Distribution|core\\.collective\\.AbstractDistribution)<(.*)>$");
			Matcher m = p.matcher(dist_type);
			if (m.find()) 
				dist_type = m.group(2);

			classContent = classContent.replace("<distribution_type>", dist_type);

			classContent = classContent.replace("<reduction>", reduction + "("
					+ this.implode(reductionParams, ", ") + ")");

			String red_type = DistributionsReductionsVisitor.reductions.get(reduction);

			p = Pattern.compile("^(core\\.collective\\.Reduction|core\\.collective\\.AbstractReduction)<(.*)>$");
			m = p.matcher(red_type);

			// Removed when reductions were refactored to handle a single type
			// String red_type2 = "";

			String red_type1 = "";

			if (m.find()) 
				red_type = m.group(2);

			int c = 0;
			int i;
			for (i = 0; i < red_type.length(); i++) {
				if (red_type.charAt(i) == '<')
					c++;
				else if (red_type.charAt(i) == '>')
					c--;
				else if (red_type.charAt(i) == ',' && c == 0)
					break;
			}

			// Removed when reductions were refactored to handle a single type
			// red_type1=red_type.substring(0, i);
			// red_type2=red_type.substring(i+1, red_type.length());
			// classContent = classContent.replace("<reduction_type1>", red_type1);
			// classContent = classContent.replace("<reduction_type2>", red_type2);

			red_type1 = red_type.substring(0, i);
			classContent = classContent.replace("<reduction_type>", red_type1);
			String taskname = createTask(e, red_type, distributionParams, this.className, distVars, params);
			
			
			classContent = classContent.replace("<task>", "new " + taskname
					+ "(" + this.implode(params.keySet(), ", ") + ")");

			return classContent;
		}
	}

	private String createTask(ExecutableElement e, String returnType,
			String[] distributionParams, String parent, List<String> distVars,
			Map<String, String> params) {
		
		String taskname = this.className + "_" + e.getSimpleName().toString() + "Task";
		String fullname = this.packageName.equals("") ? taskname : this.packageName + "." + taskname;

		Scanner s = new Scanner(ClassGenerator.class.getResourceAsStream(Constants.DISTREDTASK_TEMPLATE_FILE));		
		String task_template = s.useDelimiter("\\A").next();
		s.close();
		
		task_template = task_template.replace("<package>", "package " + this.packageName + ";");
		task_template = task_template.replace("<task_name>", taskname);
		task_template = task_template.replace("<type>", returnType);
		task_template = task_template.replace("<parent>", parent);

		List<String> imports = new ArrayList<String>(this.imports);
		String code = getCode(e, distributionParams, imports.iterator(), distVars);
		task_template = task_template.replace("<code>", code);

		if (imports.size() > 0) {
			String aux = "";
			for (String string : imports) 
				aux += string;
			task_template = task_template.replace("<imports>", aux);
		} 
		else 
			task_template = task_template.replace("<imports>", "");
		

		String[] param = new String[params.size()];
		String[] param_init = new String[params.size()];
		String[] params_fields = new String[params.size()];

		int i = 0;
		for (Entry<String, String> ee : params.entrySet()) {
			param[i] = ee.getValue() + " " + ee.getKey();
			param_init[i] = "this." + ee.getKey() + "=" + ee.getKey() + ";";
			params_fields[i] = "private " + ee.getValue() + " " + ee.getKey() + ";";
			i++;
		}

		task_template = task_template.replace("<params_fields>", this.implode(params_fields, "\n"));
		task_template = task_template.replace("<params>", this.implode(param, ", "));
		task_template = task_template.replace("<params_init>", this.implode(param_init, "\n"));

		JavaFileObject file = null;
		try {
			file = filer.createSourceFile(fullname, e);
			file.openWriter().append(task_template).close();
		} catch (IOException el) {
			// Should not happen
		}
		return taskname;
	}

	private String getCode(ExecutableElement e, String[] distributionParams,
			Iterator<String> imports, List<String> distVars) {
	
		if (treeUtils == null)
			return "";

		Tree tree = treeUtils.getTree(e);
		if (tree == null)
			return "";
		
		StringBuilder out = new StringBuilder();
		final Set<String> methodInvocation = new HashSet<String>();
		final Map<String, String> paramsType = new HashMap<String, String>();
		final Set<String> Identifiers = new HashSet<String>();

		tree.accept(new TreeScanner<Void, StringBuilder>() {
			boolean visitMethod = false;
			boolean visitblock = false;

			@Override
			public Void visitIdentifier(IdentifierTree node, StringBuilder p) {
				super.visitIdentifier(node, p);
				if (visitMethod)
					Identifiers.add(node.toString());
				return null;
			}

			@Override
			public Void visitMethodInvocation(MethodInvocationTree node, StringBuilder p) {			
				String sellection = node.getMethodSelect().toString();
				
				// invocations whose target must be modified
				if (!sellection.contains(".") || sellection.contains("this.") || 
						sellection.contains("super.")) 
					methodInvocation.add(sellection);
				return super.visitMethodInvocation(node, p);
			}

			@Override
			public Void visitMethod(MethodTree node, StringBuilder p) {
				super.visitMethod(node, p);
				for (VariableTree v : node.getParameters()) 
					paramsType.put(v.getName().toString(), v.getType().toString());
				
				return null;
			}

			@Override
			public Void visitBlock(BlockTree node, StringBuilder p) {
				if (!visitblock) {
					visitMethod = true;
					visitblock = true;
					super.visitBlock(node, p);
					Pattern pp = Pattern.compile("^\\{\\s((.*\\s)*)\\}");
					Matcher m = pp.matcher(node.toString());

					if (m.find()) 
						p.append(m.group(1));
				}
				return null;
			}
		}, out);

		String code = out.toString();

		// Obtain parameter partitions
		int i = 0;
		for (Entry<String, String> ee : paramsType.entrySet()) {
			for (String distVar : distVars)
				if (ee.getKey().equals(distVar)) {
					code = ee.getValue() + " " + ee.getKey() + " = " + 
						"(" + ee.getValue() + ") partition[" + (i++) + "];\n\t\t"
						+ code;
					break;
				}

		}

		// Modify this. and super. invocations
		for (String string : methodInvocation) {
			// FIXME: generate a method super_m for each method m invoked upon super
			if (string.contains("super.") || string.contains("this.") )  
				code = code.replace(string + "(", "this.parent." + string.split("\\.")[1] + "(");
			else 
				code = code.replace(string + "(", "this.parent." + string + "(");
		}

		return code;
	}

	private String implode(Set<String> ary, String delim) {
		return implode(ary.toArray(new String[ary.size()]), delim);
	}

	private String implode(String[] ary, String delim) {
		String out = "";
		for (int i = 0; i < ary.length; i++) {
			if (i != 0) {
				out += delim;
			}
			out += ary[i];
		}
		return out;
	}

}

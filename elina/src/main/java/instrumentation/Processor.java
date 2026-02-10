package instrumentation;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import org.kohsuke.MetaInfServices;

import com.sun.source.util.Trees;

import instrumentation.definitions.Generated;
import instrumentation.processor.ClassGenerator;
import instrumentation.processor.DistRedGenerator;
import instrumentation.processor.DistRedVisitor;
import instrumentation.processor.DistributionsReductionsVisitor;
import instrumentation.processor.PartitionedMemGenerator;
import instrumentation.processor.ServicePoolGenerator;
import instrumentation.processor.StubGenerator;


@SupportedAnnotationTypes({ "*" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)  
@MetaInfServices(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor { 

	private Filer filer;

	private Messager messager;

	private Trees treeUtils;

	private static Types typeUtils;
	
	private static String defaultTemplate; 
	
	private static ProcessingEnvironment environment;
	
	/**
	 * The TypeElement representation of the IService interface
	 */
	private static TypeElement serviceInterface = null;
	
	static {
		Scanner s = new Scanner(ClassGenerator.class.getResourceAsStream(Constants.CLASS_TEMPLATE_FILE));
		defaultTemplate = s.useDelimiter("\\A").next();
		s.close();
	}
	

	@Override
	public void init(ProcessingEnvironment env) {
		super.init(env);
		filer = env.getFiler();
		messager = env.getMessager();
		environment = env;
		
		try {
			treeUtils = Trees.instance(env);
			typeUtils = env.getTypeUtils();
			serviceInterface = env.getElementUtils().getTypeElement(Constants.SERVICE_INTERFACE);
		} catch (IllegalArgumentException e) {
			treeUtils = null;
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

		if (env.processingOver())
			return true;
		

		DistributionsReductionsVisitor visitor = new DistributionsReductionsVisitor();
		DistRedVisitor mapredvisitor = new DistRedVisitor();
		int ndists = 0;
		int nreds = 0;
		
		do {
			ndists = DistributionsReductionsVisitor.getNDists();
			nreds = DistributionsReductionsVisitor.getNReds();
			
			for (Element element : env.getRootElements()) {

				if (element.getAnnotation(Generated.class) != null)
					continue;

				if (element.getKind().equals(javax.lang.model.element.ElementKind.CLASS)) {
					element.accept(visitor, null);
					element.accept(mapredvisitor, null);
				}
				if (element.getKind().equals(javax.lang.model.element.ElementKind.INTERFACE)) 
					element.accept(mapredvisitor, null);
			}
		} while (ndists != DistributionsReductionsVisitor.getNDists()
				|| nreds != DistributionsReductionsVisitor.getNReds());

		// System.out.println(DistributionsReductionsVisitor.to_String(DistributionsReductionsVisitor.distributions));
		// System.out.println(DistributionsReductionsVisitor.to_String(DistributionsReductionsVisitor.reductions));

		for (TypeElement e : DistRedVisitor.distRedTypes.values()) {
			if (e.getAnnotation(Generated.class) != null)
				continue;
			genSource(new DistRedGenerator(messager, treeUtils, filer), e);
		}

		for (Element element : env.getRootElements()) {
			switch (element.getKind()) {
			case CLASS: {
				TypeElement clas = (TypeElement) element;
				String superclass = clas.getSuperclass().toString();
				
				if (//isConcrete(clas) &&
						(isService(clas) 
						|| superclass.equals(Constants.ACTIVE_SERVICE)
						|| superclass.equals(Constants.DIST_RED_SERVICE)
						|| superclass.equals(Constants.ABSTRACT_PARTITIONED_MEM_SERVICE)
						|| superclass.equals(Constants.MAP_PARTITIONED_MEM_SERVICE)
						|| superclass.equals(Constants.PARTITIONED_MEM_SERVICE)
						|| superclass.equals(Constants.SERVICE_POOL))) {
					
					genSource(new StubGenerator(messager), element);
			
				}

				break;
			}
			case INTERFACE: {
				if (element.getAnnotation(Generated.class) != null)
					continue;
				TypeElement clas = (TypeElement) element;
				String partitionedMemService = null;
				for (TypeMirror i : clas.getInterfaces()) {
					if (i.toString().startsWith(Constants.PARTITIONED_MEM_SERVICE))
						partitionedMemService = i.toString();
				}

				if (partitionedMemService != null) {

					String value = "";
					String key = "";

					Pattern p = Pattern.compile("^service\\.aggregator\\.PartitionedMemService<(.*),(.*)>$");
					Matcher m = p.matcher(partitionedMemService);
					if (m.find()) {
						key = m.group(1);
						value = m.group(2);
					}

					genSource(new PartitionedMemGenerator(key, value, messager), element);
				}

				if (isService(clas)) 
					genSource(new ServicePoolGenerator(messager), element);
				

				break;
			}
			
			default:
				break;
			}
		}

		return true;
	}

	private void genSourceWithTemplate(ClassGenerator gen, String templateFile, Element element) {
		Scanner s = new Scanner(ClassGenerator.class.getResourceAsStream(templateFile));
		String template = s.useDelimiter("\\A").next();
		s.close();
		
		genSource(gen, element, template);
	}
	
	private void genSource(ClassGenerator gen, Element element) {
		genSource(gen, element, defaultTemplate);
	}

	private void genSource(ClassGenerator gen, Element element, String template) {
		gen.setTemplate(template);
		String gen_clas = element.accept(gen, template);
		if (!gen.isCancelled()) {
			try {
				JavaFileObject file = filer.createSourceFile(gen.getFullClassName(), element);
				file.openWriter().append(gen.finalize(gen_clas)).close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}

	public static boolean isService(TypeElement clazz) {
		for (TypeMirror t : clazz.getInterfaces())
	        if (typeUtils.isAssignable(t, serviceInterface.asType()))
	            return true;
	    return false;
	}
	
	public static TypeElement asElement(TypeMirror t) {
		return (TypeElement) environment.getTypeUtils().asElement(t);
	}
	
}

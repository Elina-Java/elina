package instrumentation.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import instrumentation.Constants;
import instrumentation.definitions.Key;

public class PartitionedMemGenerator extends ClassGenerator {

	private String key;
	private String value;

	public PartitionedMemGenerator(String key, String value, Messager messager) {
		super(Constants.PARTITIONED_MEM_SERVICE + "<" + key + ", " + value+ ">", 
				"PartMem", messager, true, Constants.MEM_METHOD_TEMPLATE_FILE);
		
		this.key = key;
		this.value = value;
		this.excludes.add(Constants.IRECONFIGURABLE);
	}
	
	
	@Override
	protected String getConstructor(String name) {
		return "public " + name+ "(" +
					Constants.ABSTRACT_PARTITIONED_MEM_SERVICE + "<" + key + ", " + value + ">[] services," +
					Constants.PARTITIONER + "<" + key + ", " + value + "> partitioner) " +
					"{\n\t\tsuper(services, partitioner);\n\t}";
	}

	@Override
	protected String addMethodParameters(ExecutableElement e, String method) {
		method = super.addMethodParameters(e, method);
		int keys = 0;
		String key = "";
		
		for (VariableElement p : e.getParameters())
			if(p.getAnnotation(Key.class) != null) {
				keys++;
				key=p.getSimpleName().toString();
			}
				

		if (keys == 0){
			this.messager.printMessage(Kind.ERROR, "This method must declare a key", e);
			method = method.replace("<key>", "null");
		} 
		else if (keys > 1) {
			this.messager.printMessage(Kind.ERROR, "This nethod must feature a single key", e);
			method = method.replace("<key>", "null");
		}
		else
			method = method.replace("<key>", key);
		
		return method;
	}

}

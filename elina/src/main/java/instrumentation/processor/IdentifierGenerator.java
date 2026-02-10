package instrumentation.processor;

import javax.annotation.processing.Messager;

import instrumentation.Constants;



public class IdentifierGenerator extends ClassGenerator {

	public IdentifierGenerator(Messager messager) {
		super(Constants.SERVICE_IDENTIFIER, "Identifier",messager);
	}

	@Override
	protected String getConstructor(String name) {
		return "public " + name + 
				"(java.util.UUID id1, java.util.UUID id2) {\n\t\tsuper(id1,id2);\n\t}";
	}
}

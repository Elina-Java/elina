package instrumentation.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;

import instrumentation.Constants;


/**
 * Class for generating a stub for a remote Elina service
 * 
 * @author Joao Saramago
 * 
 */
public class StubGenerator extends ClassGenerator {

	public StubGenerator(Messager messager) {
		super(Constants.SERVICE_STUB, "Stub", messager);
		this.handleInnerClasses = true;
		this.excludes.add(Constants.IRECONFIGURABLE);
	}

	@Override
	protected String getConstructor(String name) {
		String const_content = 
				"public " + name + "(" +
				"java.util.UUID id1, " +
				"java.util.UUID id2, " +
				Constants.LEVEL + " l) " +
						"{\n\t\tsuper(id1,id2,l);\n\t}";
		
		const_content +=
				"\n\n\tpublic " + name + "(" +
						"java.util.UUID id1, " +
						"java.util.UUID id2, " +
						Constants.LEVEL + " l, " +
						"service.WebServiceStubGenerator wssg) "+
								"{\n\t\tsuper(id1,id2,l,wssg);\n\t}";
		
		return const_content;
	}
	
	protected List<String> getSuperClassTypeParameters() {
		List<String> typeParameters = new ArrayList<String>();
		typeParameters.add(this.className);
		return typeParameters;
	}
}

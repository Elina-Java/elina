package transitiveServiceDefinition;

import instrumentation.ProcessorTest;


public class TransitiveServiceDefinitionTest extends ProcessorTest {
	
	protected String getServiceName() {
		return SomeOtherInterface.class.getCanonicalName();
	}
	
	protected String getProviderName() {
		return SomeImplementation.class.getCanonicalName();
	}
}

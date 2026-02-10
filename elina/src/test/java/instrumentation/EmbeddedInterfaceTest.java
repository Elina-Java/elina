package instrumentation;


public class EmbeddedInterfaceTest extends ProcessorTest {
	
	protected String getServiceName() {
		return EmbeddedInterface.class.getCanonicalName();
	}
	
	protected String getProviderName() {
		return EmbeddedInterfaceProvider.class.getCanonicalName();
	}
}

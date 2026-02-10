package instrumentation;


public class SimpleServiceTest extends ProcessorTest {

	protected String getServiceName() {
		return SimpleService.class.getCanonicalName();
	}
	
	protected String getProviderName() {
		return SimpleServiceProvider.class.getCanonicalName();
	}
	
}

package instrumentation.processor;

import javax.annotation.processing.Messager;

import instrumentation.Constants;


/**
 * Class for generating a stub for a remote Web service
 * 
 * @author Herve Paulino, Paulo Dias
 * 
 */
public class WebServiceStubGenerator extends ClassGenerator {
	
	
	public WebServiceStubGenerator(Messager messager) {
		super(Constants.EXTERNAL_SERVICE_STUB, "WebServiceStub", messager, true, Constants.WEB_SERVICE_METHOD_TEMPLATE);
		this.excludes.add(Constants.IRECONFIGURABLE);
	}

	@Override 
	protected String getNewFields() {
		return "private " + this.className + " stub;";
	}
	
	@Override
	protected String getConstructor(String name) {
		String serviceName = this.className + "Service";
		String SOAPEndpoint = this.className + "ServiceSoap";		
		
		return "public " + name + "" + "(java.util.UUID uuid, java.net.URL url) {" +
					"\n\t\t super(uuid);" +
					"\n\t\tthis.stub = new " + serviceName + "(url).get" + SOAPEndpoint + "();" +
					"\n\t}";
	}	
}

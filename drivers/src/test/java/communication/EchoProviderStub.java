package communication;

/*GENERATED CLASS - DO NOT EDIT*/

import communication.EchoService;


@instrumentation.definitions.Generated
public class EchoProviderStub extends service.ServiceStub implements communication.EchoService{

	private static final long serialVersionUID = 1L;

	

	public EchoProviderStub(java.util.UUID id1, java.util.UUID id2, core.Level l) {
		super(id1,id2,l);
	}

		public java.lang.String echo (java.lang.String s) {
		try{
			service.IFuture<java.lang.String> aux = this.invoke("echo",new Object[]{s},new Class[]{java.lang.String.class});
			return aux.get();	
		}catch (service.NoSuchMethodException e) {
			return null;
		}
	}


	
	
}
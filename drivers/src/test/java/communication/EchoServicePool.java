package communication;

/*GENERATED CLASS - DO NOT EDIT*/



@instrumentation.definitions.Generated
public class EchoServicePool extends service.aggregator.ServicePool implements service.IService, EchoService{

	private static final long serialVersionUID = 1L;

	

	public EchoServicePool( service.IService[] workers, service.aggregator.IServiceScheduler scheduler) {
		super(workers, scheduler);
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
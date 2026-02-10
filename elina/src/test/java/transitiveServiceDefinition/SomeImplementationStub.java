package transitiveServiceDefinition;

@instrumentation.definitions.Generated
public class SomeImplementationStub extends service.ServiceStub<SomeImplementation> implements transitiveServiceDefinition.SomeOtherInterface{

	private static final long serialVersionUID = 1L;

	

	public SomeImplementationStub(java.util.UUID id1, java.util.UUID id2, core.Level l) {
		super(id1,id2,l);
	}

	public SomeImplementationStub(java.util.UUID id1, java.util.UUID id2, core.Level l, service.WebServiceStubGenerator wssg) {
		super(id1,id2,l,wssg);
	}

		public int newMethod () {
		try{
			service.IFuture<Integer> aux = this.invoke("newMethod",new Object[]{},new Class[]{});
			return aux.get();	
		}catch (service.NoSuchMethodException e) {
			return 0;
		}
	}


	
	
}
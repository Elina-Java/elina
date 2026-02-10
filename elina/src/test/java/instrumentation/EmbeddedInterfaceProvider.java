package instrumentation;
import service.Service;

public class EmbeddedInterfaceProvider extends Service implements EmbeddedInterface{

	@Override
	public session createSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public session createSession(int capacity) {
		// TODO Auto-generated method stub
		return null;
	}

	public class EmbebbedSession extends Service implements EmbeddedInterface.session {

		@Override
		public void put(Integer t) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Integer get() {
			// TODO Auto-generated method stub
			return null;
		}
       
    }  
}

package instrumentation;

public interface SimpleService extends service.IService {
	EmbeddedInterface createSession();
    
    Object createSession(int capacity);
 
}

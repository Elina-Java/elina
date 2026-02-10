package instrumentation;

public interface EmbeddedInterface extends service.IService {
    public abstract EmbeddedInterface.session createSession();
    
    public abstract EmbeddedInterface.session createSession(int capacity);
    
    public static interface session extends service.IService {
        public abstract void put(java.lang.Integer t);
        
        public abstract java.lang.Integer get();
    }
    
}

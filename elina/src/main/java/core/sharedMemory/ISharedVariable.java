package core.sharedMemory;

public interface ISharedVariable<V> {

	void write(V val);
	V read();
}

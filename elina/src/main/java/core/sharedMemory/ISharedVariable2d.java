package core.sharedMemory;

public interface ISharedVariable2d<V> {

	void write(V val, int x, int j);
	V read(int x, int j);
}

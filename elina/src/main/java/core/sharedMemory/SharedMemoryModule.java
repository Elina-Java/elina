package core.sharedMemory;

public class SharedMemoryModule {
	
	//Mapa com uma cópia dos valores das variáveis partilhadas registadas
	
	
	public SharedMemoryModule()
	{
	}
	
	/*
	 * Método que será invocado pelo attached variable da localidade
	 */
	public <V> void registSharedVariable(ISharedVariable<V> sv)
	{
		//TODO
	}
	
	/*
	 * Método que será invocado pelo attached variable da localidade
	 */
	public <V> void registSharedVariable(ISharedVariable2d<V> sv)
	{
		//TODO
	}
	
	
	/*
	 * 	Método que será invocado pelo sharedvariable que é invocado pelo shared variable
	 * que é passado como argumento.
	 */
	public <V> V get(ISharedVariable<V> sv)
	{
		//TODO 
		return null;
	}
	
	public <V> V get(ISharedVariable2d<V> sv)
	{
		//TODO
		return null;
	}
	
	public <V> void set(ISharedVariable<V> sv, V value)
	{
		//TODO
	}
	
	public <V> void set(ISharedVariable2d<V> sv, V value)
	{
		//TODO
	}
}

package core.collective;

/**
 * Interface that specifies a reduction
 * 
 * @author Diogo Mourão
 * @author João Saramago
 *
 * @param <T> The type of the data to reduce
 */
public interface Reduction<T> {
	
	/**
	 * 
	 * @param data Iterator over the data to reduce
	 * @return
	 */
	public T reduce(T[] data);

	void setPartitions(int length);

	int getPartitions();

}

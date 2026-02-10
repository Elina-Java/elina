package core.collective;

/**
 * Interface que representa uma distribuição
 * @author Diogo Mourão
 *
 * @param <T> Tipo de dados a distribuir
 */
public interface Distribution<T> {

	/**
	 * Partitions the input domain into nParts partitions.
	 * @param nParts the number of partitions to be produced
	 * @return the partitions
	 */
	public T[] distribution(int nParts);

	/**
	 * Define o número de partições
	 * @param length Número de partições
	 */
	public void setPartitions(int nParts);
	
	/**
	 * Returns the average size of a partition of T (in number of elements).
	 * @return size of P
	 */
	public float getAveragePartitionSize(int nParts);
	
	/**
	 * Returns the average size of line of a partition of T (in number of elements).
	 * @return size
	 */
	public float getAverageLineSize(int nParts);
	
	/** 
	 * Returns the size of an element of T (in bytes).
	 * @return size
	 */
	public int getElementSize();

}
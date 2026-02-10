package core.collective;



/**
 * Classe abstracta que representa uma distribuição com número de partições por defeito ígual ao número de trabalhadores da localidade 
 * @author João Saramago
 *
 * @param <T> Tipo de dados a distribuir
 */
public abstract class AbstractDistribution<T> implements Distribution<T> {

	private int numberOfPartitions;
	protected int numberOfElementsPerPartition;
	protected int elementarySize;

	
	public AbstractDistribution(int numberOfElementsPerPartition, int elementarySize) {
		this.numberOfElementsPerPartition = numberOfElementsPerPartition;
		this.elementarySize = elementarySize;
	}

	public void setPartitions(int nParts) {
		this.numberOfPartitions = nParts;
	}
	
	public int getPartitions() {
		return this.numberOfPartitions;
	}
	
	/**
	 *
	 * @see core.collective.Distribution#getMiminumPartitionSize()
	 */
	
	/**
	 * 
	 * @see core.collective.Distribution#getElementSize()
	 */
	@Override
	public int getElementSize()
	{
		return elementarySize;
	}

}

package core.collective;

/**
 * Classe que representa uma combinação não unívoca de distribuições
 * @author Nuno Delgado
 */
public abstract class AbstractDistributionCombination implements DistributionsCombination {
	private Distribution<?>[] distributions;

	public AbstractDistributionCombination(Distribution<?>... distributions)
	{
		this.distributions=distributions;
	}
	
	public Distribution<?>[] getDistributions()
	{
		return distributions;
	}
	
	public abstract int[][] getCombinations();
}
package elina.combinations;

import core.collective.AbstractDistributionCombination;
import core.collective.Distribution;
import elina.distributions.flatTuned.MatrixBlockCombDist;

public class MatrixDistsCombinationTuned<T> extends AbstractDistributionCombination {
	private Distribution<T>[] distributions;
	
	public MatrixDistsCombinationTuned(Distribution<T>... distributions) {
		super(distributions);
		this.distributions=distributions;
	}

	@Override
	public int[][] getCombinations() {
		int div = ((MatrixBlockCombDist) super.getDistributions()[0]).div;
		
		int c = 0;
		int[][] combs = new int[div*div*div][2];
		
		for(int i=0;i<div;i++)
			for(int j=0;j<div;j++)
			{
				int a = i*div + j;
				for(int k=0;k<div;k++)
				{
					int[] pair = combs[c++];
					pair[0] = a;
					pair[1] = j*div + k;
				}
			}
		return combs;
	}
	
	@Override
	public Distribution<T>[] getDistributions() {
		return distributions;
	}
}

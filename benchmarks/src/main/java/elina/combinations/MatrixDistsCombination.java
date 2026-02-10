package elina.combinations;

import core.collective.AbstractDistributionCombination;
import elina.distributions.Index2DBlockDist;

public class MatrixDistsCombination<T> extends AbstractDistributionCombination {

	
	public MatrixDistsCombination(Index2DBlockDist... distributions) {
		super(distributions);
	}

	@Override
	public int[][] getCombinations() {
		int div = ((Index2DBlockDist) super.getDistributions()[0]).div;
		
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
	public Index2DBlockDist[] getDistributions()
	{
		return (Index2DBlockDist[]) super.getDistributions();
	}
	
}

package elina.combinations;

import core.collective.AbstractDistributionCombination;
import elina.distributions.Index2DBlockDist;
import elina.distributions.flatTuned.MatrixBlockCombDist;

public class MatrixDistsCombinationPreallocated<T> extends AbstractDistributionCombination {

	
	public MatrixDistsCombinationPreallocated(Index2DBlockDist... distributions) {
		super(distributions);
	}

	@Override
	public int[][] getCombinations() {
		int div = ((Index2DBlockDist) super.getDistributions()[0]).div;
		
		int c = 0;
		int[][] combs = new int[div*div*div][3];
		
		for(int i=0;i<div;i++)
			for(int j=0;j<div;j++)
			{
				int a = i*div + j;
				for(int k=0;k<div;k++)
				{
					int[] triple = combs[c];
					triple[0] = a;
					triple[1] = j*div + k;
					triple[2]= c++;
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

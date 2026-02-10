package elina.combinations;

import core.collective.AbstractDistributionCombination;
import core.collective.Distribution;

public class NestedLoopCombination extends AbstractDistributionCombination{

	public NestedLoopCombination(Distribution<?>[] distrs)
	{
		super(distrs);
	}

	@Override
	public int[][] getCombinations() {
		//TODO
		int dummy = 0;
		int imax = super.getDistributions()[0].distribution(dummy).length;
		int jmax = super.getDistributions()[1].distribution(dummy).length;

		int c = 0;
		int[][] combs = new int[imax*jmax][2];

		for(int i=0;i<imax;i++)
			for(int j=0;j<jmax;j++)
			{
				int[] pair = combs[c++];
				pair[0] = i;
				pair[1] = j;
			}

		return combs;
	}
}

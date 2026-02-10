package elina.distributions.cacheOblivious;

import java.util.ArrayList;
import java.util.List;

import core.collective.AbstractDistribution;

public class TransposeMatrixDist extends AbstractDistribution<int[][]>{
	private int m, n;
	
	public TransposeMatrixDist(int m, int n, int numberOfElementsPerPartition, int elementarySize)
	{
		super(numberOfElementsPerPartition, elementarySize);
		this.m=m;
		this.n=n;
	}

	@Override
	public int[][][] distribution(int nParts) {
		int[][][] dummy = new int[1][2][2];
		return divide(0,m,0,n).toArray(dummy);
	}
	
	private List<int[][]> divide(int iBegin, int iEnd, int jBegin, int jEnd)
	{
		int m = iEnd-iBegin;
		int n = jEnd-jBegin;
		
		List<int[][]> parts = new ArrayList<int[][]>();
		//base case
		if(m==n && m==1)
		{
			int[][] part = new int[2][2];
			//i values
			part[0][0] = iBegin;
			part[0][1] = iEnd;
			//j values
			part[1][0] = jBegin;
			part[1][1] = jEnd;
			//add to partitions
			parts.add(part);
		}
		//divide
		else if(n >= m)
		{
			parts.addAll(divide(iBegin,iEnd,jBegin, jBegin + n/2));
			parts.addAll(divide(iBegin,iEnd,jBegin+n/2, jEnd));
		}
		else //m > n
		{
			parts.addAll(divide(iBegin,iBegin+m/2,jBegin, jEnd));
			parts.addAll(divide(iBegin+m/2,iEnd,jBegin, jEnd));
		}
		return parts;
	}

	@Override
	public float getAveragePartitionSize(int nParts) {
		float sqrt = (float) Math.sqrt(nParts);
		return (m/sqrt)*(n/sqrt);
	}

	@Override
	public float getAverageLineSize(int nParts) {
		float sqrt = Math.round(Math.sqrt(nParts));
		if(sqrt==Math.sqrt(nParts))
			return m/sqrt;
		else
			return 0;
	}

}

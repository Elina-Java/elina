package elina.distributions.flatTuned;

import core.collective.AbstractDistribution;
import drivers.Adapters;
import elina.utils.PartitionedTimer;

public class StencilFlatDist2D extends AbstractDistribution<int[][]> {
	
	private final int rows;
	private final int cols;

	private int div;
	private int nparts;
	
	private int k;

	public StencilFlatDist2D(int rows, int cols, int numberOfElementsPerPartition, int elementarySize, int k) {
		super(numberOfElementsPerPartition, elementarySize);
		
		this.rows=rows;
		this.cols=cols;
		this.k=k;
	}

	@Override
	public int[][][] distribution(int nparts) {
		PartitionedTimer.isFlatTuned = true;
		
		//nparts = getPartitions();
		
		int nWorkers = Adapters.getTaskExecutor().getNumberOfWorkers();

		int iRemainder, jRemainder, rowsPerPart, colsPerPart, iStep, jStep, I, J;

		while(true)
		{
			double tmp = Math.round(Math.sqrt(nparts));
			if(tmp==Math.sqrt(nparts))
			{
				div = (int) Math.round(Math.sqrt(nparts));
				//Chunks
				rowsPerPart = rows/div;
				colsPerPart = cols/div;
				
				iStep = rowsPerPart - 2*k;
				jStep = colsPerPart - 2*k;
				
				I = (rows-2*k)/iStep;
				iRemainder = (rows-2*k)%iStep;
				
				J = (cols-2*k)/jStep;
				jRemainder = (cols-2*k)%jStep;
				nparts=I*J;
				
				if(nparts%nWorkers==0)
					break;
			}
			nparts++;
		}
		
		int[][][] dist = new int[nparts][2][2];
		
		int iBegin=0;
		int iEnd;
		for(int i=0;i<I;i++)
		{
			iEnd = iBegin + colsPerPart + (iRemainder-i > 0 ? (i<I-1 ? 1 : iRemainder-i) : 0);
			
			int jBegin = 0;
			int jEnd = 0;
			for(int j=0;j<J;j++)
			{
				jEnd = jBegin + colsPerPart + (jRemainder-j > 0 ? (j<J-1 ? 1 : jRemainder-j) : 0);
				
				int[][] part = dist[i*I+j];
				part[0][0]=iBegin;
				part[0][1]=iEnd;
				part[1][0]=jBegin;
				part[1][1]=jEnd;
				jBegin = jBegin + jStep + (jRemainder-j > 0 ? 1 : 0);
			}
			iBegin = iBegin + iStep + (iRemainder-i > 0 ? 1 : 0);
		}
		
		return dist;
	}
	
	@Override
	public float getAveragePartitionSize(int nParts) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAverageLineSize(int nParts) {
		return 0;
	}

}

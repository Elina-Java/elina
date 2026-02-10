package elina.distributions.stencil;

import core.collective.AbstractDistribution;

public class StencilDist2D extends AbstractDistribution<int[][]> {

	private final int rows;
	private final int cols;

	private int div;
	private int nparts;

	private int k;

	public StencilDist2D(int rows, int cols, int numberOfElementsPerPartition, int elementarySize, int k) {
		super(numberOfElementsPerPartition, elementarySize);

		this.rows=rows;
		this.cols=cols;
		this.k=k;
	}

	@Override
	public int[][][] distribution(int nparts) {
		//nparts = getPartitions();
		
		div = (int) Math.round(Math.sqrt(nparts));

		try {
			//Chunks
			int rowsPerPart = rows/div;
			int colsPerPart = cols/div;

			int iStep = rowsPerPart - 2*k;
			int jStep = colsPerPart - 2*k;
			
			if(iStep<=0 || jStep <=0)
				throw new Exception();

			int I = (rows-2*k)/iStep;
			int iRemainder = (rows-2*k)%iStep;

			int J = (cols-2*k)/jStep;
			int jRemainder = (cols-2*k)%jStep;

			nparts=I*J;

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
		catch(Exception e) {
			System.err.println("Invalid partition size. ");
			System.exit(-1);
		}
		return null;
	}
	
	@Override
	public float getAveragePartitionSize(int nParts) {
		float sqrt = (float) Math.sqrt(nParts);
		return (rows/sqrt)*(cols/sqrt);
	}

	@Override
	public float getAverageLineSize(int nParts) {
		float sqrt = Math.round(Math.sqrt(nParts));
		if(sqrt==Math.sqrt(nParts))
			return rows/sqrt;
		else
			return 0;
	}
}

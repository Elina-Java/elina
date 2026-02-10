package elina.distributions;


import core.collective.AbstractDistribution;


public class IndexDist2D extends AbstractDistribution<int[]> {

	final int ncols;
	final int nrows;
	
	public IndexDist2D(int ncols, int nrows, int unitSize, int elementarySize) {
		super(unitSize, elementarySize);
		this.ncols = ncols;
		this.nrows = nrows;
	}
	
	public IndexDist2D(int ncols, int nrows, int elementarySize) {
		this(ncols, nrows, elementarySize, elementarySize);
	}
	
	public int[][] distribution(int workers) {
		int[][] dist = new int[workers][4];
	
		int rworkers = (int) Math.sqrt(workers);
	//	System.out.println("workers " + workers);
	//	System.out.println("rworkers " + rworkers);
		int[][] rows = oneDim(nrows, rworkers);
		int[][] cols = oneDim(ncols, workers/rworkers);

		int k = 0;
		for (int i = 0; i < rows.length; i++)
			for (int j = 0; j < cols.length; j++)	{
				dist[k][0] = rows[i][0];
				dist[k][1] = rows[i][1];
				dist[k][2] = cols[j][0];
				dist[k][3] = cols[j][1];	
				k++;
			}
				
		return dist;
	}

	private final int[][] oneDim(int length, int workers) {
		int[][] dist = new int[workers][2];
		final int perPlace = length / workers;
		final int remainder =length % workers;

		for (int k = 0; k < workers; k++) {
			dist[k][0] = k * perPlace + java.lang.Math.min(k, remainder);
			dist[k][1] = dist[k][0]  + perPlace + (remainder - k > 0 ? 1 : 0);
		}
		return dist;
	}
	
	@Override
	public float getAveragePartitionSize(int nParts) {
		float sqrt = Math.round(Math.sqrt(nParts));
		return (ncols/sqrt)*(nrows/sqrt);
	}

	@Override
	public float getAverageLineSize(int nParts) {
		float sqrt = Math.round(Math.sqrt(nParts));
		if(sqrt==Math.sqrt(nParts))
			return ncols/sqrt;
		else
			return 0;
	}
}

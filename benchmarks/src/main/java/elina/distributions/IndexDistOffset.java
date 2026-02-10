package elina.distributions;




public class IndexDistOffset extends IndexDist {

	private final int offset;
	private final int total;	
	
	public IndexDistOffset(int length, int offset, int total, int unitSize) {
		super(length, unitSize);
		this.offset = offset;
		this.total = total;
	}
	
	public int[][] distribution() {
		final int workers = getPartitions();
		int[][] dist = new int[workers][2];
		
		final int perPlace = length / workers;
		final int remainder =length % workers;

		//System.out.println(perPlace +  "   " + remainder);
		
		
		for (int k = 0; k < workers; k++) {
			dist[k][0]  = java.lang.Math.min(k * perPlace + java.lang.Math.min(k, remainder) + offset, total);
		//	System.out.println("offset" + offset + "  k " + k + " co " + (remainder - k > 0 ? 1 : 0) );
			dist[k][1]  = java.lang.Math.min(dist[k][0]  + perPlace + (remainder - k > 0 ? 1 : 0), total);
		}
		return dist;
		
	}


}

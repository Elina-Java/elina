	package javaGrande.elina.multicore.sparsematmult;


import core.collective.AbstractDistribution;
import elina.utils.SizeOf;


public class SparseDist extends AbstractDistribution<int[]> {

	private final  int [] row;
	private final  int [] col;
	private final  double [] val;
	private final double[] x;
	private final double[] y;

	private final static int SIZE = SizeOf.Double + SizeOf.Double + SizeOf.Int + SizeOf.Int + SizeOf.Double;
	
	public SparseDist(double y[], double x[], int row[], int col[], double val[]) {
		super(SIZE, SIZE);
		this.y = y;
		this.x = x;
		this.row = row;
		this.col = col;
		this.val = val;
	}

	public int[][] distribution(final int nthreads) {
		//final int nthreads = getPartitions();
		final int[][] partitions = new int[nthreads+1][2];
		  int [] ilow = new int[nthreads];
		  int [] iup = new int[nthreads];
		  int [] sum = new int[nthreads+1];
		  int [] rowt = new int[row.length];
		  int [] colt = new int[col.length];
		  double [] valt = new double[val.length];
	     int sect;

	    

	// reorder arrays for parallel decomposition

	    sect = (y.length + nthreads-1)/nthreads;

	    for (int i=0; i<nthreads; i++) {
	      ilow[i] = i*sect;
	      iup[i] = ((i+1)*sect)-1;
	      if(iup[i] > y.length) iup[i] = y.length;
	    }

	    for (int i=0; i<row.length; i++) {
	      for (int j=0; j<nthreads; j++) {
	        if((row[i] >= ilow[j]) && (row[i] <= iup[j])) { 
	          sum[j+1]++; 
	        }
	      }         
	    }

	    for (int j=0; j<nthreads; j++) {
	      for (int i=0; i<=j; i++) {
	         partitions[j][0] = partitions[j][0] + sum[j-i];
	         partitions[j][1] = partitions[j][1] + sum[j-i];
	      }
	    }

	    for (int i=0; i<row.length; i++) {
	      for (int j=0; j<nthreads; j++) {
	        if((row[i] >= ilow[j]) && (row[i] <= iup[j])) {
	          rowt[partitions[j][1]] = row[i];
	          colt[partitions[j][1]] = col[i];
	          valt[partitions[j][1]] = val[i];
	          partitions[j][1]++;
	        }
	      }
	    }

	    for (int i=0; i<row.length; i++) {
	      row[i] = rowt[i];
	      col[i] = colt[i];
	      val[i] = valt[i];
	    }
	    return partitions;
	}

	public int getNumberOfElements() {
		return y.length*SizeOf.Double + x.length*SizeOf.Double + row.length*SizeOf.Int + col.length*SizeOf.Int + val.length*SizeOf.Double;
	}

	@Override
	public float getAveragePartitionSize(int nParts) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAverageLineSize(int nParts) {
		// TODO Auto-generated method stub
		return 0;
	}

}

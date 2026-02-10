package imageProcessing.blur;

import service.SOMDTask;

public class BlurTask extends SOMDTask<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[][] t0, t1;

	public BlurTask(double[][] t0, double[][] t1) {
		this.t0 = t0;
		this.t1 = t1;
	}

	@Override
	public Void call(Object[] partition) {
		int iBeginT0 = ((int[][]) partition[0])[0][0];
		int iEndT0 = ((int[][]) partition[0])[0][1];
		int jBeginT0 = ((int[][]) partition[0])[1][0];
		int jEndT0 = ((int[][]) partition[0])[1][1];

		for(int i=iBeginT0+1; i<iEndT0-1; i++)
			for(int j=jBeginT0+1; j<jEndT0-1; j++)
			{
				t1[i][j] = t0[i][j] / 2 + 
						t0[i+1][j]/16 + 
						t0[i-1][j]/16 + 
						t0[i][j-1]/16 + 
						t0[i][j+1]/16 +
						t0[i+1][j+1]/16 + 
						t0[i-1][j+1]/16 + 
						t0[i+1][j-1]/16 + 
						t0[i-1][j+1]/16;	
			}

		//Top
		if(iBeginT0==0)
		{
			for(int j=jBeginT0+1; j<jEndT0-1; j++)
			{
				t1[0][j] = t0[0][j] / 2 + 
						t0[1][j+1]/10 + 
						t0[1][j-1]/10 + 
						t0[1][j]/10 + 
						t0[0][j-1]/10 + 
						t0[0][j+1]/10;
			}
		}

		//Bottom
		if(iEndT0==t0.length)
		{
			for(int j=jBeginT0+1; j<jEndT0-1; j++)
			{
				t1[iEndT0-1][j] = t0[iEndT0-1][j] / 2 + 
						t0[iEndT0-2][j+1]/10 + 
						t0[iEndT0-2][j-1]/10 + 
						t0[iEndT0-2][j]/10 + 
						t0[iEndT0-1][j-1]/10 + 
						t0[iEndT0-1][j+1]/10;
			}
		}

		//Left
		if(jBeginT0==0)
		{
			if(iBeginT0==0)
				t1[0][0] = t0[0][0] / 2 + t0[0][1]/6 + t0[1][1]/6 + t0[1][0]/6;

			if(iEndT0==t0.length)
				t1[t0.length-1][0] = t0[t0.length-1][0] / 2 + 
				t0[t0.length-1][1]/6 + 
				t0[t0.length-2][1]/6 + 
				t0[t0.length-1][0]/6;

			for(int i=iBeginT0+1; i<iEndT0-1; i++)
			{
				t1[i][0] = t0[i][0] / 2 + 
						t0[i][1]/10 + 
						t0[i+1][1]/10 + 
						t0[i-1][1]/10 + 
						t0[i-1][0]/10 + 
						t0[i+1][0]/10;
			}
		}

		//Right
		if(jEndT0==t0[0].length)
		{
			if(iBeginT0==0)
				t1[0][t0[0].length-1] = t0[0][t0[0].length-1] / 2 + 
				t0[0][t0[0].length-2]/6 + 
				t0[1][t0[0].length-2]/6 + 
				t0[1][t0[0].length-1]/6;

			if(iEndT0==t0.length)
			{
				t1[t0.length-1][t0[0].length-1] = t0[t0.length-1][t0[0].length-1] / 2 + 
						t0[t0.length-1][t0[0].length-2]/6 + 
						t0[t0.length-2][t0[0].length-2]/6 + 
						t0[t0.length-1][t0[0].length-1]/6;
			}

			for(int i=iBeginT0+1; i<iEndT0-1; i++)
			{
				t1[i][t0[0].length-1] = t0[i][t0[0].length-1] / 2 + 
						t0[i][t0[0].length-2]/10 + 
						t0[i+1][t0[0].length-2]/10 + 
						t0[i-1][t0[0].length-2]/10 + 
						t0[i-1][t0[0].length-1]/10 + 
						t0[i+1][t0[0].length-1]/10;
			}
		}

		return null;
	}

}

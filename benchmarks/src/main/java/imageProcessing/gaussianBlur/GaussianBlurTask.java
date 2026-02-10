package imageProcessing.gaussianBlur;

import service.SOMDTask;

public class GaussianBlurTask extends SOMDTask<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[][] w, t0, t1;
	private int r;

	public GaussianBlurTask(double[][] weightMatrix, int radius, double[][] t0, double[][] t1) {
		this.w = weightMatrix;
		this.t0 = t0;
		this.t1 = t1;
		this.r = radius;
	}

	/*
	@Override
	public int getDynamicDataSize(int nparts) {
		int div = (int) Math.round(Math.sqrt(nparts));
		int rowsPerPart = t0.length/div;
		int colsPerPart = t0[0].length/div;
		//por excesso
		return (rowsPerPart+1-2*r)*(colsPerPart+1-2*r)*SizeOf.Double;
	}*/


	@Override
	public Void call(Object[] partition) {
		int iBegin = ((int[][]) partition[0])[0][0];
		int iEnd = ((int[][]) partition[0])[0][1];
		int jBegin = ((int[][]) partition[0])[1][0];
		int jEnd = ((int[][]) partition[0])[1][1];

		double sum;

		//Inner
		for(int i=iBegin+r; i<iEnd-r; i++)
			for(int j=jBegin+r; j<jEnd-r; j++)
			{
				sum = 0;
				for(int ii=-r;ii<=r;ii++)
					for(int jj=-r;jj<=r;jj++)
						sum += w[i+ii][j+jj];
				double v = 0;
				for(int ii=-r;ii<=r;ii++)
					for(int jj=-r;jj<=r;jj++)
					{
						v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
					}
				t1[i][j] = v;
			}

		//Top
		if(iBegin==0)
		{
			for(int i=0;i<r;i++)
				for(int j=jBegin+r; j<jEnd-r; j++)
				{
					sum = 0;
					for(int ii=-i;ii<=r;ii++)
						for(int jj=-r;jj<=r;jj++)
							sum += w[i+ii][j+jj];
					double v = 0;
					for(int ii=-i;ii<=r;ii++)
						for(int jj=-r;jj<=r;jj++)
						{
							v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
						}
					t1[i][j] = v;
				}

			//Top - Left
			if(jBegin==0)
			{
				for(int i=0;i<r;i++)
					for(int j=0; j<r; j++)
					{
						sum = 0;
						for(int ii=-i;ii<=r;ii++)
							for(int jj=-j;jj<=r;jj++)
								sum += w[i+ii][j+jj];
						double v = 0;
						for(int ii=-i;ii<=r;ii++)
							for(int jj=-j;jj<=r;jj++)
							{
								v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
							}
						t1[i][j] = v;
					}
			}

			//Top - Right
			if(jEnd==t0[0].length)
			{
				for(int i=0;i<r;i++)
					for(int j=jEnd-r; j<jEnd; j++)
					{
						sum = 0;
						for(int ii=-i;ii<=r;ii++)
							for(int jj=-r;jj<=jEnd-j-1;jj++)
								sum += w[i+ii][j+jj];
						double v = 0;
						for(int ii=-i;ii<=r;ii++)
							for(int jj=-r;jj<=jEnd-j-1;jj++)
							{
								v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
							}
						t1[i][j] = v;
					}
			}
		}

		//Left
		if(jBegin==0)
		{
			for(int i=iBegin+r;i<iEnd-r;i++)
				for(int j=0; j<r; j++)
				{
					sum = 0;
					for(int ii=-r;ii<=r;ii++)
						for(int jj=-j;jj<=r;jj++)
							sum += w[i+ii][j+jj];
					double v = 0;
					for(int ii=-r;ii<=r;ii++)
						for(int jj=-j;jj<=r;jj++)
						{
							v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
						}
					t1[i][j] = v;
				}
		}

		//Bottom
		if(iEnd==t0.length)
		{
			for(int i=iEnd-r;i<iEnd;i++)
				for(int j=jBegin+r; j<jEnd-r; j++)
				{
					sum = 0;
					for(int ii=-r;ii<=iEnd-i-1;ii++)
						for(int jj=-r;jj<=r;jj++)
							sum += w[i+ii][j+jj];
					double v = 0;
					for(int ii=-r;ii<=iEnd-i-1;ii++)
						for(int jj=-r;jj<=r;jj++)
						{
							v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
						}
					t1[i][j] = v;
				}

			//Bottom - Left
			if(jBegin==0)
			{
				for(int i=iEnd-r;i<iEnd;i++)
					for(int j=0; j<r; j++)
					{
						sum = 0;
						for(int ii=-r;ii<=iEnd-i-1;ii++)
							for(int jj=-j;jj<=r;jj++)
								sum += w[i+ii][j+jj];
						double v = 0;
						for(int ii=-r;ii<=iEnd-i-1;ii++)
							for(int jj=-j;jj<=r;jj++)
							{
								v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
							}
						t1[i][j] = v;
					}
			}

			//Bottom - Right
			if(jEnd==t0[0].length)
			{
				for(int i=iEnd-r;i<iEnd;i++)
					for(int j=jEnd-r; j<jEnd; j++)
					{
						sum = 0;
						for(int ii=-r;ii<=iEnd-i-1;ii++)
							for(int jj=-r;jj<=jEnd-j-1;jj++)
								sum += w[i+ii][j+jj];
						double v = 0;
						for(int ii=-r;ii<=iEnd-i-1;ii++)
							for(int jj=-r;jj<=jEnd-j-1;jj++)
							{
								v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
							}
						t1[i][j] = v;
					}
			}
		}

		//Right
		if(jEnd==t0[0].length)
		{
			for(int i=iBegin+r;i<iEnd-r;i++)
				for(int j=jEnd-r; j<jEnd; j++)
				{
					sum = 0;
					for(int ii=-r;ii<=r;ii++)
						for(int jj=-r;jj<=jEnd-j-1;jj++)
							sum += w[i+ii][j+jj];
					double v = 0;
					for(int ii=-r;ii<=r;ii++)
						for(int jj=-r;jj<=jEnd-j-1;jj++)
						{
							v+= t0[i+ii][j+jj]*(w[i+ii][j+jj]/sum);
						}
					t1[i][j] = v;
				}
		}

		return null;
	}

}

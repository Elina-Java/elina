package imageProcessing.sequential;

import java.io.IOException;

import imageProcessing.gaussianBlur.GaussianBlurData;
import imageProcessing.gaussianWeight.GaussianWeightProvider;
import imageProcessing.gaussianWeight.GaussianWeightService;
import imageProcessing.gaussianWeight.GaussianWeightUtils;
import elina.ApplicationLauncher;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class GaussianBlur {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int nexecs = 1;
		int radius = 1;

		if(args.length>=2)
		{
			nexecs=Integer.parseInt(args[0]);
			if(args[1].contains("-"))
			{
				String[] nr = args[1].split("-");
				GaussianBlurData.Matrix_sizes[0] = Integer.parseInt(nr[0]);
				radius = Integer.parseInt(nr[1]);
			}
			else
				GaussianBlurData.Matrix_sizes[0] = Integer.parseInt(args[1]);
		}

		int size = GaussianBlurData.Matrix_sizes[0];
		double sigma = 1.5;

		double[][] t0 = GaussianBlurData.createMatrix(size);
		double[][] t1 = GaussianBlurData.createMatrix(size);
		double[][] weightMatrix = new double[t0.length][t0[0].length];


		GaussianWeightUtils.computeWeightMatrix(weightMatrix, sigma);

		Evaluation eval = new Evaluation(nexecs); 

		eval.writeAppName("Gaussian_Blur"+"_radius="+radius, size);
		for (int j = 0; j < nexecs; j++) {
			eval.startTimer();

			gaussianBlur(radius, weightMatrix, t0, t1);

			eval.stopTimer();
			eval.writeTime();
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();

		eval.end();
	}

	private static void gaussianBlur(int r, double[][] w, double[][] t0, double[][] t1) {
		int iBegin = 0;
		int iEnd = w.length;
		int jBegin = 0;
		int jEnd = w[0].length;

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
	}

}

package imageProcessing.sequential;

import java.io.IOException;

import imageProcessing.blur.BlurData;
import elina.utils.Evaluation;
import elina.utils.PartitionedTimer;

public class Blur {

	public static void main(String[] args) throws IOException {
		int nexecs = 1;

		if(args.length==2)
		{
			BlurData.Matrix_sizes[0]=Integer.parseInt(args[1]);
			nexecs=Integer.parseInt(args[0]);
			PartitionedTimer.NEXECS=nexecs;
		}

		int size = BlurData.Matrix_sizes[0];
		BlurData mdata = new BlurData();
		double[][] t0 = mdata.createMatrix(size);
		double[][] t1 = mdata.createMatrix(size);

		Evaluation eval = new Evaluation(nexecs); 

		eval.writeAppName("Blur_Sequential", size);
		for (int j = 0; j < nexecs; j++) {
			eval.startTimer();

			blur(t0, t1);

			eval.stopTimer();
			eval.writeTime();
			System.gc();
		}
		eval.writeAverage();
		eval.writeSTDV();

		eval.end();
	}

	public static void blur(double[][] t0, double[][] t1)
	{
		int iBeginT0 = 0;
		int iEndT0 = t0.length;
		int jBeginT0 = 0;
		int jEndT0 = t0[0].length;

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

		for(int j=jBeginT0+1; j<jEndT0-1; j++)
		{
			t1[0][j] = t0[0][j] / 2 + 
					t0[1][j+1]/10 + 
					t0[1][j-1]/10 + 
					t0[1][j]/10 + 
					t0[0][j-1]/10 + 
					t0[0][j+1]/10;
		}

		for(int j=jBeginT0+1; j<jEndT0-1; j++)
		{
			t1[iEndT0-1][j] = t0[iEndT0-1][j] / 2 + 
					t0[iEndT0-2][j+1]/10 + 
					t0[iEndT0-2][j-1]/10 + 
					t0[iEndT0-2][j]/10 + 
					t0[iEndT0-1][j-1]/10 + 
					t0[iEndT0-1][j+1]/10;
		}
		t1[0][0] = t0[0][0] / 2 + t0[0][1]/6 + t0[1][1]/6 + t0[1][0]/6;

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

		t1[0][t0[0].length-1] = t0[0][t0[0].length-1] / 2 + 
				t0[0][t0[0].length-2]/6 + 
				t0[1][t0[0].length-2]/6 + 
				t0[1][t0[0].length-1]/6;

		t1[t0.length-1][t0[0].length-1] = t0[t0.length-1][t0[0].length-1] / 2 + 
				t0[t0.length-1][t0[0].length-2]/6 + 
				t0[t0.length-2][t0[0].length-2]/6 + 
				t0[t0.length-1][t0[0].length-1]/6;

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

}

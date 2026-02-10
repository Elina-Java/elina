package linearAlgebra.multithreaded.matrixMult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatrixMult {

	private int n_parts;
	private ThreadPoolExecutor executor;
	private int [][] result;
	private Future<Void> [] activities;

	public MatrixMult(int n_parts, int n_workers)
	{
		this.n_parts = n_parts;
		this.executor = new ThreadPoolExecutor(n_workers, n_workers, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
		this.executor.allowCoreThreadTimeOut(false);
		this.executor.prestartAllCoreThreads();
		this.activities = new Future[n_parts];
	}
	
	
	public int[][] matrixMult(int[][] m1, int[][] m2) {
	
		int m1_size = m1.length;
		this.result = new int[m1.length][m2[0].length];
		
		for(int partition=0; partition < n_parts; partition++)
		{
			int li = (partition * m1_size) / n_parts;
			int lf = ( ( ( partition + 1 ) * m1_size) / n_parts) - 1;
			this.activities[partition] = this.executor.submit(new MultTask(m1, m2, result, li, lf));
		}
		
		for(Future f: this.activities)
			try {
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		this.executor.shutdown();	
		return result;
	}
	
	
	public static void main(String [] args)
	{
//		int [][] m1 = new int [][] {  {8,3,0,1}, {1,2,3,4}, {9,8,7,6}, {9,8,7,6}  };
//		int [][] m2 = new int [][] { {5,4,3,1}, {4,4,3,1}, {3,4,3,1}, {1,4,3,1} };
		
		
		//int n = 1024; //A
//		int n = 2048; //B
		//int n = 2500; //C
		int n = 3000; //D
//		int n = 3500; //E
//		int n = 4096; //F
		
		
		for(int i=0; i < 5; i++)
		{
			long t1 = System.currentTimeMillis();
			
			int [][] m1 = new int[n][n];
			int [][] m2 = new int[n][n];
			
			for(int k=0; k < n; k++)
				for(int w=0; w < n; w++)
				{
					m1[k][w] = k;
					m2[k][w] = w;
				}
			MatrixMult mm = new MatrixMult(4, 4);
		
			
		
			mm.matrixMult(m1, m2);
			long t2 = System.currentTimeMillis();
			System.out.println("Time in ms = "+(t2 - t1));
		}
//		for(int i=0; i < res.length; i++)
//			{
//				for(int j=0; j < res[0].length; j++)
//					System.out.print(res[i][j]+"\t");
//
//				System.out.println();
//			}
	}
}

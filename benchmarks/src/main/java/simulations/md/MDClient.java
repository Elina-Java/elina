package simulations.md;


import java.io.IOException;
import java.util.Random;

import elina.utils.Evaluation;




public class MDClient  {

	private final MDService  service;
	private final int EXECS;

	public MDClient(MDService service, int nexecs) {
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() throws IOException {
		
		int N = MDData.Matrix_sizes[0];
		
		//INITS
		float blockIndex1=0.0f;
		float blockIndex2=0.0f;
		float blockCount=1.0f;
		int Nsteps=1;
		
		Float3[] accelCur = new Float3[N];
		for(int i=0;i<N;i++)
		{
			accelCur[i] = new Float3(0.0f,0.0f,0.0f);
		}
		
		Float3[] pos = new Float3[N];
		Float3[] vel = new Float3[N];
		float[] mass = new float[N];
		getSnapshot(pos,vel,mass);
		
		
		for (int i = 0; i < MDData.NUMBER_TESTS; i++) {

			Evaluation eval = new Evaluation(EXECS); 

			eval.writeAppName("Md", i);
			for (int j = 0; j < EXECS; j++) {
				
				eval.startTimer();
				for(int s=0;s<Nsteps;s++)
					service.accel(pos, pos, mass, mass, blockIndex1, blockIndex2, blockCount, accelCur);
				eval.stopTimer();
				eval.writeTime();
				System.gc();
			}
			eval.writeAverage();
		}
		
	}
	
	private void getSnapshot(Float3[] pos, Float3[] vel, float[] mass)
	{
		Random r = new Random(1010101010);
		for (int i = 0; i < pos.length; i++ )
		{
			pos[i] = new Float3();
			pos[i].x = r.nextFloat();
			pos[i].y = r.nextFloat();;
			pos[i].z = r.nextFloat();;

			mass[i] = r.nextFloat();;

			vel[i] = new Float3();
			vel[i].x = r.nextFloat();;
			vel[i].y = r.nextFloat();;
			vel[i].z = r.nextFloat();;
		}
	}
	
}

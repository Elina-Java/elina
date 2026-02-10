package hierarchy;

import org.junit.Test;

public class Chunking {

	@Test
	public void testChunking()
	{
		int nworkers = 3;
		int worksize = 11;
		int remainder = worksize%nworkers;
		System.out.println("remainder: "+remainder);
		int tasksPerThread=worksize/nworkers;

		for(int etid=0;etid<nworkers;etid++)
		{
			int begin = (remainder-etid>=0 ? (tasksPerThread+1)*etid : (tasksPerThread+1)*remainder+tasksPerThread*(etid-remainder));
			int end = begin + tasksPerThread + (remainder-etid>0 ? 1: 0);
			System.out.println("tid: "+etid + "| begin: "+begin + " end: "+ end + " total: "+ (end-begin));
		}
	}
}

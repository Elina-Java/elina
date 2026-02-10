package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import elina.utils.Evaluation;

public class PartitionedTimerTest {

	@Test
	public void Test1() 
	{
		double[] v = {1.823, 1.822, 1.822, 1.821};
		
		double avg = Evaluation.average(v);
		
		assertEquals(avg, 1.822, 0);
		assertEquals(Evaluation.standardDeviation(v, avg), 7.071067811865482E-4, 1E-16);
	}
	
	@Test
	public void Test2()
	{
		double[] v = {0.06286, 0.130608};
		
		double avg = Evaluation.average(v);
		
		assertTrue(avg == 0.096734);
		assertTrue(Evaluation.standardDeviation(v, avg) == 0.033874);
	}

}

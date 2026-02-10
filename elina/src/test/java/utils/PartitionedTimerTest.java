package utils;

import elina.utils.Evaluation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PartitionedTimerTest {

	@Test
	public void Test1()  {
		double[] v = {1.823, 1.822, 1.822, 1.821};
		
		double avg = Evaluation.average(v);
		
		assertEquals(1.822, avg, 0);
		assertEquals(7.071067811865482E-4, Evaluation.standardDeviation(v, avg), 1E-16);
	}
	
	@Test
	public void Test2()  {
		double[] v = {0.06286, 0.130608};
		
		double avg = Evaluation.average(v);

        assertEquals(0.096734, avg);
        assertEquals(0.033874, Evaluation.standardDeviation(v, avg));
	}

}

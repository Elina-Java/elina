package taskExecutor;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;

import core.Place;
import elina.ApplicationLauncher;
import elina.Elina;

public class TaskExecutorPoolThreadsTest {

	private static Place place;
	
	@BeforeClass
	 public static void launchElina() {
		ApplicationLauncher.init();
		place = Elina.getPlace();
	 }
		
//	@Test
	public void testExecute() {
		long result = place.spawn(new Fib(10)).get();
		assertEquals((long) 55, result);
	}

}

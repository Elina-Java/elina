package taskExecutor;



import core.Place;
import elina.ApplicationLauncher;
import elina.Elina;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskExecutorPoolThreadsTest {

	private static Place place;
	
	@BeforeAll
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

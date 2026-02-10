package taskExecutor;

import service.IFuture;
import service.Task;
import core.Place;
import elina.Elina;

public class Fib extends Task<Long> {
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Input parameter: fib(n)
	 */
	private int n;
	
	/**
	 * Constructor
	 * @param n Input parameter: fib(n)
	 */
	public Fib(int n) {
		this.n = n;
	}

	/**
	 * The task's behavior 
	 */
	public Long call() {
		if (n <= 1)
		return (long) n;

	Place place = Elina.getPlace();
	IFuture<Long> f1 = place.spawn(new Fib(n - 1));
	IFuture<Long> f2 = place.spawn(new Fib(n - 2));
	return f1.get() + f2.get();
	
	}
}

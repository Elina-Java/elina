package aggregation;

import java.util.UUID;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import service.IFuture;
import core.collective.Reduction;

public class DistRedFuture<R> implements IFuture<R> {

	private R[] results;
	private Reduction<R> red;
	private UUID id = UUID.randomUUID();
	private Phaser completed;

	public DistRedFuture(Phaser completed, R[] results, Reduction<R> red) {
		this.completed=completed;
		this.red = red;
		this.results=results;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return true;
	}

	@Override
	public R get() {
		this.completed.arriveAndAwaitAdvance();
		R aux = red.reduce(results);
		return aux;
	}

	@Override
	public R get(long timeout, TimeUnit unit) {
		return this.get();
	}

	@Override
	public boolean isCancelled() {
		return true;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public UUID getID() {
		return id;
	}

}

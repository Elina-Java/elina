package service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface IFuture<R> {

	public boolean cancel(boolean mayInterruptIfRunning);
	public R get();
	public R get(long timeout, TimeUnit unit);
	public boolean isCancelled();
	public boolean isDone();
	public UUID getID();
}

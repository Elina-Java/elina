package core;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import service.IFuture;



public class MultipleFutures<T> implements IFuture<T> {

	List<IFuture<T>> futures;
	
	public MultipleFutures(List<IFuture<T>> mains) {
		this.futures=mains;
	}

	
	public boolean cancel(boolean mayInterruptIfRunning) {
		for (IFuture<T> f : this.futures) {
			f.cancel(mayInterruptIfRunning);
		}
		return false;
	}

	
	public T get() {
		for (IFuture<T> f : this.futures) {
			f.get();
		}
		return null;
	}

	
	public T get(long timeout, TimeUnit unit) {
		return this.get();
	}

	
	public boolean isCancelled() {
		return false;
	}

	
	public boolean isDone() {
		return false;
	}

	
	public UUID getID() {
		return null;
	}

}

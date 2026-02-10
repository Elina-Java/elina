package service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Future<R> implements IFuture<R>
{
	protected java.util.concurrent.Future<R> future;
	protected UUID id;

	public Future(java.util.concurrent.Future<R> future)
	{
		this.future = future;
		this.id=UUID.randomUUID();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.future.cancel(mayInterruptIfRunning);
	}


	public R get(){
		try {
			return this.future.get();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}


	public R get(long timeout, TimeUnit unit){
		try {

			return this.future.get(timeout, unit);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public boolean isCancelled() {
		return this.future.isCancelled();
	}


	public boolean isDone() {
		return this.future.isDone();
	}

	
	public UUID getID() {
		return this.id;
	}
}

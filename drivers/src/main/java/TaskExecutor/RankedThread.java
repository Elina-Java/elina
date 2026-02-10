package TaskExecutor;

public class RankedThread extends Thread {
	protected int rank;
	
	public RankedThread(Runnable r, int rank)
	{
		super(r);
		this.rank=rank;
	}

}

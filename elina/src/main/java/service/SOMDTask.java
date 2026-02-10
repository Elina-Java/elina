package service;

public abstract class SOMDTask<R> extends Schedulable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract R call(Object[] partition);
	
	
	public static int getRank() {
		String tname = Thread.currentThread().getName();
		int tid = Integer.valueOf(tname.substring(tname.indexOf('-')+1, tname.length()));
		return tid-3;
	}

	public int getDynamicDataSize(int length) {
		return 0;
	}

}

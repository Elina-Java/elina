package service.aggregator;



public class RoundRobinScheduler implements IServiceScheduler {

	private int size;
	private int pos = 0;
	
	@Override
	public int getIndex() {
		int out = pos;
		pos = (pos+1)%size;
		return out;
	}

	@Override
	public void setSize(int length) {
		this.size=length;
	}
}

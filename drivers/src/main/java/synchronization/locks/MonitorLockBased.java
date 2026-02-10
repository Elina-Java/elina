package synchronization.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import service.ConditionCode;
import service.ICondition;
import service.IMonitor;




public class MonitorLockBased 
implements IMonitor{

	private Lock monitorLock;
	
	public MonitorLockBased()
	{
		this.monitorLock = new ReentrantLock();
	}
	
	@Override
	public void beginAtomic() {
		this.monitorLock.lock();
		
	}

	@Override
	public void endAtomic() {
		this.monitorLock.unlock();
	}

	@Override
	public ICondition newCondition(ConditionCode conditionTest) {
		return new Condition(monitorLock, conditionTest);
	}

}

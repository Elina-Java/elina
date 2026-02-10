package synchronization;

import service.IMonitor;
import synchronization.locks.MonitorLockBased;
import drivers.MonitorDriver;

public class MonitorDriverLock 
implements MonitorDriver{

	
	@Override
	public IMonitor createMonitor() {
		return new MonitorLockBased();
	}

}

package core.synchronization;

import drivers.Adapters;
import drivers.BarrierDriver;
import drivers.MonitorDriver;
import service.IBarrier;
import service.IMonitor;

/**
 * @author Diogo Mourão
 * @author João Saramago
 *
 */
public class SyncModule {
	
	public IMonitor createMonitor()
	{
		MonitorDriver driver=Adapters.getMonitorDriver();
		if (driver!=null)
				return driver.createMonitor();
		else
			return null;
	}
	
	public IBarrier createBarrier(){
		BarrierDriver driver=Adapters.getBarrierDriver();
		if (driver!=null)
				return driver.createBarrier();
		else
			return null;
	}
	
	public IBarrier createBarrier(IBarrier b){
		BarrierDriver driver=Adapters.getBarrierDriver();
		if (driver!=null)
				return driver.createBarrier(b);
		else
			return null;
	}
	
}

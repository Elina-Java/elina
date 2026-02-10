package drivers;

import service.IBarrier;

public interface BarrierDriver {

	IBarrier createBarrier();
	//IBarrier createBarrier(int n);
	IBarrier createBarrier(IBarrier b);

}

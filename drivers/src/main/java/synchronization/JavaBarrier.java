package synchronization;

import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import service.IBarrier;
import drivers.BarrierDriver;

public class JavaBarrier implements BarrierDriver {

//	@Override
//	public IBarrier createBarrier(int n) {
//		return new JavaBarrierImp(n);
//	}

	@Override
	public IBarrier createBarrier() {
		return new JavaBarrierImp(0);
	}

	@Override
	public IBarrier createBarrier(IBarrier b) {
		return new JavaBarrierImp(0);
	}

}


class JavaBarrierImpParent extends JavaBarrierImp{

	private IBarrier parent;

	public JavaBarrierImpParent(int arg0,IBarrier parent) {
		super(arg0);
		this.parent=parent;
		this.parent.register();
	}
	
	@Override
	public void await() {
		super.await();
		parent.await();
	}
	@Override
	public void register() {
		super.register();
		this.parent.register();
	}
	
}

class JavaBarrierImp implements IBarrier{

	CyclicBarrier aux;
	UUID id;
	
	public JavaBarrierImp(int n){
		if(n!=0)
			aux=new CyclicBarrier(n);
		id=UUID.randomUUID();
	}
	
	@Override
	public void await() {
		try {
			if(aux!=null)
				aux.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void register() {
		if(aux==null)
			aux=new CyclicBarrier(1);
		else{
			aux=new CyclicBarrier(aux.getParties()+1);
		}
		
	}

	@Override
	public UUID getID() {
		return id;
	}
	
}



//class JavaBarrierImp implements IBarrier{
//
//	CyclicBarrier aux;
//	int count;
//	
//	public JavaBarrierImp(int n){
//		this.count=n;
//	}
//	
//	@Override
//	public void await() {
//		synchronized (this) {	
//			if(aux==null)
//				aux=new CyclicBarrier(count);
//		}
//		try {
//				aux.await();
//		} catch (InterruptedException | BrokenBarrierException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public void register() {
//		count++;
//	}
//	
//}

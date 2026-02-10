package synchronization;

import service.IBarrier;
import drivers.BarrierDriver;

public class CopyOfJavaBarrier implements BarrierDriver {

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


//class JavaBarrierImpParent extends JavaBarrierImp{
//
//	private IBarrier parent;
//
//	public JavaBarrierImpParent(int arg0,IBarrier parent) {
//		super(arg0);
//		this.parent=parent;
//		this.parent.register();
//	}
//	
//	@Override
//	public void await() {
//		super.await();
//		parent.await();
//	}
//	@Override
//	public void register() {
//		super.register();
//		this.parent.register();
//	}
//	
//}
//
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
//		if(aux==null)
//			aux=new CyclicBarrier(count);
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

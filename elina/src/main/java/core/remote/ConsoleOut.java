package core.remote;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import elina.Elina;
import service.RemoteException;
import service.Task;


public class ConsoleOut extends PrintStream {

	
	private Map<Thread, Task<?>> taskmap = new HashMap<Thread, Task<?>>();
	private Map<UUID, IRemoteOutConsole> serviceMap = new HashMap<UUID, IRemoteOutConsole>();
	
	
	public void Associate(Thread t,Task<?> tt){
		taskmap.put(t, tt);
	}
	
	public void registerRemoteConsole(UUID id,IRemoteOutConsole console){
		serviceMap.put(id, console);
	}
	
	
	public ConsoleOut(OutputStream out) {
		super(out);
	}
	
	@Override
	public void print(Object obj) {
		this.print(obj.toString());
	}
	@Override
	public void print(long l) {
		this.print(Long.toString(l));
	}
	
	@Override
	public void print(float f) {
		this.print(Float.toString(f));
	}
	
	@Override
	public void print(double d) {
		this.print(Double.toString(d));
	}
	
	@Override
	public void print(char[] s) {
		this.print(new String(s));
	}
	@Override
	public void print(char c) {
		this.print(c+"");
	}
	
	@Override
	public void print(boolean b) {
		this.print(Boolean.toString(b));
	}
	
	@Override
	public void print(int i) {
		this.print(Integer.toString(i));
	}
	
	@Override
	public void print(String s) {
//		if(this.conf!=null){
//			for (@SuppressWarnings("rawtypes") Node n : this.conf.getNodes()) {
//				if(n.isLocal()){
//					s="["+n.getAddr()+"]: "+s;
//				}
//			}
//		}
		
		
		Task<?> aux = taskmap.get(Thread.currentThread());
		if(aux!=null){
			try {
				serviceMap.get(aux.getService().getClientId()).print(s);
			} catch (RemoteException e) {
				Elina.shutdown(aux.getService().getClientId());
				return;
			}catch (Exception e) {
				super.print(s);
				return;
			}
		}else{
			super.print(s);
		}
		//super.print(Thread.currentThread());
	}
	
	@Override
	public void println() {
		this.print("\n");
	}
	
	@Override
	public void println(String x) {
		this.print(x+"\n");
	}
	
	@Override
	public void println(boolean x) {
		this.println(Boolean.toString(x));
	}
	@Override
	public void println(char x) {
		this.println(x+"");
	}
	@Override
	public void println(char[] x) {
		this.println(new String(x));
	}
	@Override
	public void println(double x) {
		this.println(Double.toString(x));
	}
	@Override
	public void println(float x) {
		this.println(Float.toString(x));
	}
	@Override
	public void println(int x) {
		this.println(Integer.toString(x));
	}
	@Override
	public void println(long x) {
		this.println(Long.toString(x));
	}
	@Override
	public void println(Object x) {
		if(x==null)
			this.println("null");
		else
			this.println(x.toString());
	}
	



	public void unregisterRemoteConsole(UUID id) {
		this.taskmap.remove(id);
		
	}

}

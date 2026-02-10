package core.remote;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import elina.Elina;
import service.RemoteException;
import service.Task;


public class ConsoleIn extends BufferedInputStream {

	private Map<Thread, Task<?>> taskmap = new HashMap<Thread, Task<?>>();
	private Map<UUID, IRemoteInConsole> serviceMap = new HashMap<UUID, IRemoteInConsole>();
	
	public ConsoleIn(InputStream in){
		super(in);
	}

	public void registerRemoteConsole(UUID id, IRemoteInConsole remoteConsole) {
		serviceMap.put(id, remoteConsole);
		
	}

	public void Associate(Thread t, Task<?> taskInterface) {
		taskmap.put(t, taskInterface);
		
	}
	
	
	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		Task<?> aux = taskmap.get(Thread.currentThread());
		if(aux!=null){
			try {
				RemoteRead bb=serviceMap.get(aux.getService().getClientId()).read(off, len);
				bb.copyto(b,off);
				return bb.getSize();
			} catch (RemoteException e) {
				Elina.shutdown(aux.getService().getClientId());
				return -1;
			}
			catch (Exception e) {
				return super.read(b,off,len);
			}
		}else{
			return super.read(b, off, len);
		}
	}
	
	@Override
	public int read() throws IOException {
		Task<?> aux = taskmap.get(Thread.currentThread());
		if(aux!=null){
			try {
				return serviceMap.get(aux.getService().getClientId()).read();
			} catch (RemoteException e) {
				Elina.shutdown(aux.getService().getClientId());
				return -1;
			}
		}else{
			return super.read();
		}
	}



	public void unregisterRemoteConsole(UUID id) {
		this.serviceMap.remove(id);
		
	}

}

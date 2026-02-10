package naming;

import java.rmi.Remote;
import java.rmi.RemoteException;

import service.ServiceStub;

public interface Registry extends Remote {
	
	<T extends ServiceStub> void register (String id, T resource) throws RemoteException;
	<T extends service.IService> T lookup(String id) throws RemoteException;

}

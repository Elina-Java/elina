package core.communication;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.UUID;

import elina.Elina;
import service.IService;


public class ServiceIdentifier implements Serializable{

	private static final long serialVersionUID = 1L;
	private UUID clientID;
	private UUID id;
	private String type;

	public ServiceIdentifier(UUID id,UUID clientID,String type){
		this.clientID=clientID;
		this.id=id;
		this.type=type;
	}
	
	
	
	public UUID getClientId() {
		return this.clientID;
	}

	
	public UUID getID() {
		return this.id;
	}
	
	public Object readResolve() throws ObjectStreamException{
		try{
		IService p = Elina.getService(clientID,id);
		return p;
		}catch (NullPointerException e) {
			try {
				Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(type);
				
				return cl.getDeclaredConstructor().newInstance();
			} catch (Exception e2) {
				return null;
			}
		}
	}
}

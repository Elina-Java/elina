package service;

import java.util.UUID;

public interface IBarrier {

	public void await();
	
	public void register();
	//public void unregister();

	public UUID getID();
	
}

package service;

import java.io.Serializable;
import java.util.UUID;

import core.Place;
import core.communication.CommunicationModule;
import core.communication.IComEvent;
import core.communication.Message;
import core.communication.MessageTag;
import core.communication.Node;
import elina.Elina;

public class BarrierStub implements IBarrier,IComEvent,Serializable {

	private static final long serialVersionUID = 1L;
	private UUID id;
	private UUID bid;
	@SuppressWarnings("rawtypes")
	private Node node;
	private CommunicationModule comm;
	
	private boolean await=false;
	
	public BarrierStub(IBarrier b,Place p){
		bid=b.getID();
		this.comm = Elina.getCommunicationModule();
		node = comm.getLocalNode();
		id=UUID.randomUUID();
	}
	
	
	
	@Override
	public synchronized void await() {
		comm.sendMessage(null, bid, id, MessageTag.BARRIER_AWAIT, node);
		try{
			while(!await)
				this.wait();
		}catch (Exception e) {}
	}

	@Override
	public void register() {
		
		comm.sendMessage(null, bid, id, MessageTag.BARRIER_REGISTER, node);
	}

	@Override
	public synchronized <C> void processMessage(Message<C> sms) {
		switch (sms.getType()) {
		case BARRIER_AWAKE:
			this.await=true;
			notifyAll();
			break;
		default:
			break;
		}
		
	}

	@Override
	public UUID getID() {
		return id;
	}

}

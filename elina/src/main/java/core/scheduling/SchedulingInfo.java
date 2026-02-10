package core.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import core.communication.Node;
import service.DummyApplication;
import service.Service;
import service.ServiceStub;



@SuppressWarnings("rawtypes")
public class SchedulingInfo implements Serializable{

	

	private static final long serialVersionUID = 1L;
	private Set<Node> nodes=new HashSet<Node>();
	private Map<Node,List<Service>> serviceSchedule=new HashMap<Node,List<Service>>();
	private Map<Node,List<ServiceStub>> stubSchedule=new HashMap<Node,List<ServiceStub>>();
	
	public void setSchedule(Service p, Node next) {
		nodes.add(next);
		
		List<Service> aux;

		aux = serviceSchedule.get(next);
		
		if(aux==null)
			aux=new ArrayList<Service>();
		
		aux.add(p);
		
		serviceSchedule.put(next,aux);
		
	}
	
	public void finish(){
		for (Entry<Node, List<Service>> sch : this.serviceSchedule.entrySet()) {
			finishProcessNodes(sch);
		}
	}

	private void finishProcessNodes(Entry<Node, List<Service>> sch) {
		for (Service p : sch.getValue()) {
			if (p.getClass().getName().startsWith("service.Application$") || p instanceof DummyApplication){
				continue;
			}
			
			if (p instanceof Service) {
				ServiceStub stub=p.createStub();
				stub.setLocation(sch.getKey());
			
				for (Node n : this.nodes) {
					if(n.equals(sch.getKey()))
						continue;
				
					List<ServiceStub> aux = stubSchedule.get(n);
					if(aux==null)
						aux=new ArrayList<ServiceStub>();
				
					aux.add(stub);
					stubSchedule.put(n,aux);
				}
			}
			
		}
	}

	public Set<Node> getNodes() {
		return nodes;
	}
	
	public List<Service> getSchedule(Node n){
		List<Service> aux =serviceSchedule.get(n);
		if(aux==null)
			aux=new ArrayList<Service>(0);
		return aux;
	}
	
	
	public List<ServiceStub> getStubSchedule(Node n){
		List<ServiceStub> aux =stubSchedule.get(n);
		if(aux==null)
			aux=new ArrayList<ServiceStub>(0);
		return aux;
	}
	
	@Override
	public String toString() {
		String out="";
		for (Entry<Node, List<Service>> aux : this.serviceSchedule.entrySet()) {
			out+="["+aux.getKey()+": ";
			for (Service p : aux.getValue()) {
				out+=p;
			}
			out+="]";
		}
		return out;
	}

}

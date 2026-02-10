package scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import service.ActiveService;
import service.Application;
import service.IService;
import service.Service;
import core.communication.Node;
import core.scheduling.SchedulingAdapter;
import core.scheduling.SchedulingInfo;

public class AggregatorSch implements SchedulingAdapter{

	@SuppressWarnings("rawtypes")
	@Override
	public SchedulingInfo schedule(Application app, List<Node<?>> sort) {
		
		Service active=null;
		Service aggregator=null;
		
		List<Service> places=new ArrayList<Service>();
		
		
		for (IService iPlace : app.getServices()) {
			if(iPlace instanceof ActiveService)
				active=(Service)iPlace;
			else if(iPlace.getClass().getCanonicalName().endsWith("DistRed"))
				aggregator=(Service)iPlace;
			else
				places.add((Service)iPlace);
		}
		
		Iterator<Node<?>> it = sort.iterator();
		
		
		SchedulingInfo si=new SchedulingInfo();
		
		boolean fist=true;
		for (Service iPlace : places) {
					
			if(!it.hasNext())
				it = sort.iterator();
			
			Node n=it.next();
			
			if(fist){
				if(active!=null)
					si.setSchedule(active, n);
				if(aggregator!=null)
					si.setSchedule(aggregator, n);
				fist=false;
			}
			si.setSchedule(iPlace, n);
		}
		
		
		return si;
	}

}

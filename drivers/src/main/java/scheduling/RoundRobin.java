package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import service.Application;
import service.IService;
import service.Service;
import core.communication.Node;
import core.scheduling.SchedulingAdapter;
import core.scheduling.SchedulingInfo;
import elina.Elina;

public class RoundRobin implements SchedulingAdapter {

	private static Logger logger = LogManager.getLogger(RoundRobin.class);

	@Override
	@SuppressWarnings("rawtypes")
	public SchedulingInfo schedule(Application app, List<Node<?>> sort) {
		if (Elina.DEBUG)
			logger.debug("Scheduling Nodes [nodes " + sort.toString()
					+ "] [App " + app.toString() + "]");

		if (sort.size() == 0)
			return null;

		final Map<IService, List<IService>> affinities = new HashMap<IService, List<IService>>();
		Map<IService, Set<IService>> toMerge = new HashMap<IService, Set<IService>>();
		Map<IService, IService> mergeWith = new HashMap<IService, IService>();

		for (IService p : app.getServices()) {
			List<UUID> affinity = ((Service) p).getAffinity();
			
			if (affinity != null && affinity.size() > 0) {
				
				if(affinities.get(p)==null)
					affinities.put(p,new ArrayList<IService>(affinity.size()));
				
				affinities.get(p).add(p);
				
				for (UUID uuid : affinity) {
					IService pp = app.getService(uuid);
					
					if(affinities.get(pp)==null)
						affinities.put(pp, new ArrayList<IService>());
					
					affinities.get(pp).add(p);
					
					
					affinities.get(p).add(pp);
				}
				
			} else {
				if(affinities.get(p)==null)
					affinities.put(p, new ArrayList<IService>());
				affinities.get(p).add(p);
			}
		}

//		for (IPlace p : app.getPlaces()) {
//			List<UUID> affinity = ((Place) p).getAffinity();
//			if (affinity != null && affinity.size() > 0) {
//				for (UUID uuid : affinity) {
//					IPlace pp = app.getPlace(uuid);
//					for (Entry<IPlace, List<IPlace>> iPlace : affinities
//							.entrySet()) {
//						if (iPlace.getValue().contains(pp)) {
//							IPlace to_mer = iPlace.getKey();
//							
//							while (mergeWith.get(to_mer) != null)
//								to_mer = mergeWith.get(to_mer);
//
//							if(toMerge.get(pp)!=null){
//								toMerge.get(pp).add(to_mer);
//							}else{
//								if(toMerge.get(to_mer)==null)
//									toMerge.put(to_mer,new HashSet<IPlace>());
//								
//								toMerge.get(to_mer).add(pp);
//							}
//							mergeWith.put(pp, to_mer);
//						}
//					}
//				}
//			}
//		}
		
		
		for (IService p : app.getServices()) {
			List<UUID> affinity = ((Service) p).getAffinity();
			for (UUID uuid : affinity) {
				IService pp = app.getService(uuid);
				for (Entry<IService, List<IService>> iPlace : affinities.entrySet()) {
					
					
					if(iPlace.getKey()==p)
						continue;
					
					
					
					if(iPlace.getValue().contains(pp)){
						IService to_mer = iPlace.getKey();
						
						while (mergeWith.get(to_mer) != null)
							to_mer = mergeWith.get(to_mer);
						
						if(toMerge.get(to_mer)==null)
							toMerge.put(to_mer,new HashSet<IService>());
						
						if(!to_mer.equals(pp)){
							toMerge.get(to_mer).add(pp);
						
							mergeWith.put(pp, to_mer);
						}
					}
					
					
					
					
				}
			}
		}
		
		
		
		List<IService> places=new ArrayList<IService>(app.getServices());
		Collections.sort(places, new Comparator<IService>(){
			@Override
			public int compare(IService o1, IService o2) {
				return (affinities.get(o1).size()<affinities.get(o2).size() ? -1 : (affinities.get(o1).size()==affinities.get(o2).size() ? 0 : 1));
			}
			
		});
		
		SchedulingInfo out = new SchedulingInfo();
		Iterator<Node<?>> it = sort.iterator();
		
		Set<IService> visited=new HashSet<IService>();
		
		for (IService p : app.getServices()) {
			if(mergeWith.get(p)==null){
				Set<IService> aux = new HashSet<IService>();
				
				aux.add(p);
				aux.addAll(affinities.get(p));
				if(toMerge.get(p)!=null){
					aux.addAll(toMerge.get(p));
					
					for (IService iPlace : toMerge.get(p)) {
						aux.addAll(affinities.get(iPlace));
					}
				}
				
				if(!it.hasNext())
					it = sort.iterator();
				
				
				Node n =null;
				for (IService iPlace : aux) {
					if(!visited.contains(iPlace)){
						if(n==null)
							n=it.next();
						out.setSchedule((Service)iPlace, n);
					}
				}
				visited.addAll(aux);
			}
		}
		
//		for (Entry<IPlace, Set<IPlace>> aff : toMerge.entrySet()) {
//			Set<IPlace> aux = new HashSet<IPlace>();
//			aux.add(aff.getKey());
//			aux.addAll(affinities.get(aff.getKey()));
//			aux.addAll(aff.getValue());
//			for (IPlace iPlace : aff.getValue()) {
//				aux.addAll(affinities.get(iPlace));
//			}
//			
//			if(!it.hasNext())
//				it = sort.iterator();
//			
//			Node n = it.next();
//			for (IPlace iPlace : aux) {
//				out.setSchedule((Place)iPlace, n);
//			}
//		}
		return out;
		
		

		// Iterator<Node> it = sort.iterator();
		// Set<Place> visited=new HashSet<Place>();
		// SchedulingInfo out = new SchedulingInfo();
		//
		//
		// for (IPlace p : app.getPlaces()) {
		// List<UUID> affinity = ((Place)p).getAffinity();
		// if(affinity!=null && affinity.size()>0){
		//
		// if(!it.hasNext())
		// it = sort.iterator();
		//
		// Node n = it.next();
		// for (UUID uuid : affinity) {
		// IPlace pp = app.getPlace(uuid);
		// out.setSchedule((Place)pp, n);
		// visited.add((Place)pp);
		// }
		//
		// if(visited.contains(p))
		// continue;
		//
		// out.setSchedule((Place)p, n);
		// visited.add((Place)p);
		// }
		// }
		//
		//
		//
		//
		// for (IPlace p : app.getPlaces()) {
		//
		// if(visited.contains(p))
		// continue;
		//
		// if(!it.hasNext())
		// it = sort.iterator();
		//
		// Node n = it.next();
		//
		//
		// out.setSchedule((Place)p,n);
		// visited.add((Place)p);
		// }
		//
		// return out;
//		return null;
	}

}

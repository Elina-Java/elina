package drivers;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import core.Level;
import core.init.Configuration;
import core.scheduling.SchedulingAdapter;
import core.scheduling.SchedulingRankAdapter;

public class Adapters {

	/* drivers a utilizar */
	private static CloningDriver cloner;
	private static MonitorDriver monitorFactory;
	@SuppressWarnings("rawtypes")
	private static Map<Level,DomainDecompositionDriver> distRed = new HashMap<Level, DomainDecompositionDriver>();
	private static CommunicationDriver commDriver;
	private static SchedulingAdapter schDriver;
	private static SchedulingRankAdapter schRankDriver;
	private static TaskExecutorDriver taskExecutorDriver;
	private static SchedulingDriver SchedulingDriver;
	
	//TODO
	private static HierarchyReadDriver hierarchyReadDriver;
	private static Class<?> hierarchyReadClass;
	private static PartitioningDriver partitioningDriver;
	private static Class<?> partitioningClass;
	private static AffinityMapperDriver affinityMapperDriver;
	private static Class<?> affinityMapperClass;
	private static Class<?> aggregatorClass;
	
	private static BarrierDriver barrierFactory;
	
	/**
	 * The logging adapter
	 */
	private static Logger logger;
	
	private static int nworkers;
	private static Class<?> taskClass;

	@SuppressWarnings("rawtypes")
	public static void selectdrivers(Configuration conf) {

		try {
			Class<?> cl;
			
			// Mandatory adapters

			cl = Class.forName(conf.getLogger());
			logger = (Logger) cl.newInstance();
			
			
			
			
			String copy = conf.getCopy();
			String monitor = conf.getSynchronization();
			String barrier = conf.getBarrier();
			String comm = conf.getCommunication();
			
			
			
			
			Map<Level,String> mr = new HashMap<Level, String>();
			
			for (Level l : Level.values()) {
				String aux = conf.getDomainDecomposition(l);
				if(aux!=null)
					mr.put(l,aux);
			}
			
			String sch = conf.getScheduling();
			String schrank=conf.getSchRank();
			String task=conf.getTaskExecutor();
			
			//TODO
			String hierR = conf.getHierarchyReader();
			Adapters.hierarchyReadClass=Class.forName(hierR);
			hierarchyReadDriver = createHierarchyReader();
			
			//TODO
			String partitioner = conf.getPartitioner();
			Adapters.partitioningClass=Class.forName(partitioner);
			partitioningDriver = createPartitioner();
			
			//TODO
			String affinityMapper = conf.getAffinityMapper();
			Adapters.affinityMapperClass=Class.forName(affinityMapper);
			affinityMapperDriver = createAffinityMapper();
			
			//TODO
			String aggregator = conf.getAggregator();
			Adapters.aggregatorClass=Class.forName(aggregator);
			SchedulingDriver = createAggregator();
			
			int nworkers=conf.getNworkers();
			
			Adapters.nworkers=nworkers;
			if(System.getProperty("core.middleware.nworkers")!=null)
				nworkers=Adapters.nworkers=Integer.parseInt(System.getProperty("core.middleware.nworkers"));

			cl = Class.forName(task);
			Adapters.taskClass=Class.forName(task);
		
			taskExecutorDriver = createTaskExecutor(nworkers);
			

			
			/*
			 * TODO - Fazer as respetivas inicializações aos drivers, caso seja
			 * necessário.
			 */
			
			
			if(copy!=null){
				cl = Class.forName(copy);
				cloner = (CloningDriver) cl.newInstance();
			}

			if(barrier!=null){
				cl = Class.forName(monitor);
				monitorFactory = (MonitorDriver) cl.newInstance();
			}
			
			if(barrier!=null){
				cl = Class.forName(barrier);
				barrierFactory = (BarrierDriver) cl.newInstance();
			}

			if(comm!=null){
				cl = Class.forName(comm);
				commDriver = (CommunicationDriver) cl.newInstance();
			}

			for (Entry<Level, String> e : mr.entrySet()) {
				cl = Class.forName(e.getValue());
				distRed.put(e.getKey(), (DomainDecompositionDriver) cl.newInstance());
			}
			
			if(sch!=null){
				cl=Class.forName(sch);
				schDriver=(SchedulingAdapter)cl.newInstance();
			}
			
			if(schrank!=null){
				cl=Class.forName(schrank);
				schRankDriver=(SchedulingRankAdapter)cl.newInstance();
			}
			
			
			

		} catch (ClassNotFoundException e) {
			System.out.println("Class not found: "+e.getMessage());
			// e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			// e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	public static MonitorDriver getMonitorDriver() {
		return monitorFactory;
	}

	public static CloningDriver getCloningDriver() {
		return cloner;
	}

	public static CommunicationDriver getCommDriver() {
		return commDriver;
	}
	
	public static SchedulingAdapter getSchDriver(){
		return schDriver;
	}
	
	public static SchedulingRankAdapter getSchRankDriver(){
		return schRankDriver;
	}
	
	public static HierarchyReadDriver getHierarchyReadDriver()
	{
		return hierarchyReadDriver;
	}
	
	public static PartitioningDriver getPartitioningDriver()
	{
		return partitioningDriver;
	}

	@SuppressWarnings("unchecked")
	public static <R> DomainDecompositionDriver getDomainDecompositionDriver(Level l) {
		return distRed.get(l);
	}

	public static TaskExecutorDriver createTaskExecutor(int n_workers) {
		try{
			return  (TaskExecutorDriver) taskClass.getConstructor(int.class).newInstance(nworkers);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//TODO
	public static HierarchyReadDriver createHierarchyReader()
	{
		try {
			@SuppressWarnings("all")
			HierarchyReadDriver tmp = (HierarchyReadDriver) hierarchyReadClass.getConstructor(new Class<?>[]{}).newInstance(new Class<?>[]{});
			return tmp;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static PartitioningDriver createPartitioner()
	{
		try {
			@SuppressWarnings("all")
			PartitioningDriver tmp = (PartitioningDriver) partitioningClass.getConstructor(new Class<?>[]{}).newInstance(new Class<?>[]{});
			return tmp;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static AffinityMapperDriver createAffinityMapper()
	{
		try {
			@SuppressWarnings("all")
			AffinityMapperDriver tmp = (AffinityMapperDriver) affinityMapperClass.getConstructor(new Class<?>[]{}).newInstance(new Class<?>[]{});
			return tmp;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private static SchedulingDriver createAggregator() {
		try {
			@SuppressWarnings("all")
			SchedulingDriver tmp = (SchedulingDriver) aggregatorClass.getConstructor(new Class<?>[]{}).newInstance(new Class<?>[]{});
			return tmp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//TODO
	public static TaskExecutorDriver getTaskExecutor() {
		return taskExecutorDriver;
	}
	
	public static AffinityMapperDriver getAffinityMapper() {
		return affinityMapperDriver;
	}
	
	public static SchedulingDriver getSchedulingDriver() {
		return SchedulingDriver;
	}

	public static BarrierDriver getBarrierDriver() {
		return barrierFactory;
	}
	
	/**
	 * Retrieve the logging adapter
	 * @return
	 */
	public static Logger getLogger() {
		return logger;
	}

}

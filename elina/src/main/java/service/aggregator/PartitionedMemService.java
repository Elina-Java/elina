package service.aggregator;

import java.io.Serializable;

import core.Level;
import service.IFuture;
import service.NoSuchMethodException;
import service.Service;

public class PartitionedMemService<K,V> extends Service implements Serializable{

	private static final long serialVersionUID = 1L;
	private IPartitioner<K,V> partitioner;
	
	public PartitionedMemService(AbstractPartitionedMemService<K, V>[] services,IPartitioner<K,V> partitioner){
		super();
		this.level=Level.Cluster;
		this.partitioner=partitioner;
		this.partitioner.setServices(services);
		for (AbstractPartitionedMemService<K, V> p : services) {
			p.setPartitioner(partitioner);
		}
	}
	
	public <R> IFuture<R> invoke(K key,String methodName, Object... args) throws NoSuchMethodException{
		AbstractPartitionedMemService<K, V> service=this.partitioner.getService(key);
		return service.invoke(methodName, args);
	}
	
}

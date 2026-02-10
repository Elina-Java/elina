package service.aggregator;


import service.Service;

public abstract class AbstractPartitionedMemService<K, V> extends Service implements PartitionedMem<K,V> {

	private static final long serialVersionUID = 1L;
	private IPartitioner<K,V> partitioner;
	
	public AbstractPartitionedMemService(){
		super();
	}

	public IPartitioner<K,V> getPartitioner() {
		return partitioner;
	}

	public void setPartitioner(IPartitioner<K,V> partitioner) {
		this.partitioner = partitioner;
	}
	
	
	public V getValue(K key) {
		AbstractPartitionedMemService<K,V> p=this.partitioner.getService(key);
		return p.get(key);
	}
	
	public void setValue (K key, V value) {
		AbstractPartitionedMemService<K,V> p= this.partitioner.getService(key);
		p.set(key, value);
	}
}

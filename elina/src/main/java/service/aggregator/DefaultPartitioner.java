package service.aggregator;

public class DefaultPartitioner<K,V> implements IPartitioner<K,V>{

	private AbstractPartitionedMemService<K,V>[] services;
	
	@Override
	public void setServices(AbstractPartitionedMemService<K,V>[] services) {
		this.services=services;
		
	}

	@Override
	public AbstractPartitionedMemService<K,V> getService(K key) {
		int i=key.hashCode();
		return this.services[i%services.length];
	}

}

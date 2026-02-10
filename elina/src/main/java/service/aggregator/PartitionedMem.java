package service.aggregator;

import service.IService;


public interface PartitionedMem<K, V> extends IService{
	
	public V get(K key);
	
	public void set (K key, V value);

}

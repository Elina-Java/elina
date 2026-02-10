package service.aggregator;



/**
 * 
 * @author João Saramago
 * @since Dec 6, 2011 4:08:36 PM
 * @param <T>
 */
public interface IPartitioner<K,V> {

	public void setServices(AbstractPartitionedMemService<K,V>[] services);
	
	public AbstractPartitionedMemService<K,V> getService(K key);
	
}

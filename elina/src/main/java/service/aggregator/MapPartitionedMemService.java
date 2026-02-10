package service.aggregator;

import java.util.HashMap;
import java.util.Map;

public abstract class MapPartitionedMemService<K, V> extends AbstractPartitionedMemService<K, V>{

	private static final long serialVersionUID = 1L;
	private Map<K, V> map=new HashMap<K, V>();
	
	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public void set(K key, V value) {
		this.map.put(key, value);
	}

}

package elina.reductions;

import core.collective.AbstractReduction;

/**
 * Computes the maximum of an array of comparable items
 * 
 * @author Eduardo Marques
 *
 * @param <T>
 */
public class MaxReduce<T extends Comparable<T>> extends AbstractReduction<T>   {

	public T reduce(final T[] results) {
		T min = null;
		for (int i = 0; i < results.length; i++) {
			T res = results[i];
			if (min == null || res.compareTo(min) == 1)
				min = res;
		}
		return min;
	}
}


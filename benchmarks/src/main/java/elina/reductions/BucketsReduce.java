package elina.reductions;

import core.collective.AbstractReduction;


public class BucketsReduce extends AbstractReduction<int[]>{
	
	
	public int[] reduce(int[][] array) {
		int buckets = array[0].length;
		int[] result = new int[buckets];
		
		for(int i = 0; i < array.length; i++)
			for(int j = 0; j < buckets; j++)
				result[j] += array[i][j];
		return result;
	}

}

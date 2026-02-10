package elina.reductions;

import core.collective.AbstractReduction;

public class MergeArrayReduce extends AbstractReduction<int[]>{

	public int[] reduce(int[][] array) {
		int size = 0;
		for(int i = 0; i < array.length; i++)
			size += array[i].length;
		
		int[] result = new int[size];
		int[] indexs = new int[array.length];
		int count = 0;
		for(int i = 0; i < size; i++) {
			int min = Integer.MAX_VALUE;
			int index = 0;
			for(int j = 0; j < indexs.length; j++) {
				int k = indexs[j];
				if(k < array[j].length && array[j][k] < min) {
					min = array[j][k];
					index = j;
				}
			}
			result[count++] = min;
			indexs[index]++;
		}
		return result;
	}
	
}

package sort.merge;

import service.Service;
import elina.distributions.IndexDist;
import elina.reductions.MergeArrayReduce;
import elina.utils.SizeOf;

public class SortServer extends Service implements SortService {

	public int[] mergeSort(int[] vector) {
		return distReduce(new MergeSortTask(vector),  new MergeArrayReduce(), new IndexDist(vector.length, 1, SizeOf.Int)).get();	
		
	}
	
}

package sort.merge;

import java.util.Arrays;
import service.SOMDTask;


/**
 * 
 * @author Adapted by Eduardo Marques and Herve Paulino
 * 
 */

public class MergeSortTask extends SOMDTask<int[]> {

	/**
	 * Default Serial Version
	 */
	private static final long serialVersionUID = 1L;

	private final int[] array;

	MergeSortTask(int[] vector) {
		this.array = vector;
	}

	public int[] call(Object[] partition) {
		final int begin = ((int[]) partition[0])[0];
		final int end = ((int[]) partition[0])[1];
		
		int[] aux = Arrays.copyOfRange(array, begin, end);
		mergeSort(aux, 0, aux.length - 1);
		return aux;
	}

	private void mergeSort(int[] array, int begin, int end) {
		if (begin < end) {
			int mid = (begin + end) / 2;
			mergeSort(array, begin, mid);
			mergeSort(array, mid + 1, end);
			merge(array, begin, mid, end);
		}
	}

	private void merge(int[] array, int begin, int mid, int end) {

		int size = end - begin + 1;

		int[] temp = new int[size];

		System.arraycopy(array, begin, temp, 0, size);

		int i = 0;
		int j = mid - begin + 1;

		for (int pos = 0; pos < size; pos++) {
			if (j <= size - 1) {
				if (i <= mid - begin) {
					if (temp[i] < temp[j]) {
						array[begin + pos] = temp[i++];
					} else {
						array[begin + pos] = temp[j++];
					}
				} else {
					array[begin + pos] = temp[j++];
				}
			} else {
				array[begin + pos] = temp[i++];
			}
		}
	}
}
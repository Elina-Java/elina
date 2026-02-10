package sort.merge;

import java.io.IOException;
import java.util.Random;

import elina.utils.Evaluation;



public class MergeSortClient {

	// Problem Parameters
	static int[] sizes = {100000, 1000000, 10000000, 100000000};
	static int NUMBER_OF_PROBLEMS = sizes.length;
	
	// Benchmark Parameters
	private int EXECS;
	private SortService service;

	public MergeSortClient(SortService service, int nexecs) {
		super();
		this.service = service;
		this.EXECS = nexecs;
	}

	public void run() {
		try {
			for (int size : sizes) {

				Evaluation eval = new Evaluation(EXECS); 

				eval.writeAppName("mergeSort", size);
				for (int j = 0; j < EXECS; j++) {
						
					int[] array = initData(size);

					eval.startTimer();

					service.mergeSort(array);

					eval.stopTimer();
					eval.writeTime();
				}
				eval.writeAverage();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private int[] initData(int size) {
		Random r = new Random(0);
		int[] array = new int[size];
		for(int i = 0; i < array.length; i++)
			array[i] = r.nextInt(20000000);
		return array;
	}



}

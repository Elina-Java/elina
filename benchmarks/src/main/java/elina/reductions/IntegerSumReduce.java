package elina.reductions;

import core.collective.AbstractReduction;


public class IntegerSumReduce extends AbstractReduction<Integer> {

	public Integer reduce(Integer[] results) {
		int result = 0;
		for (int i = 0; i < results.length; i++) 
			result += results[i];
		return result;
	}

}
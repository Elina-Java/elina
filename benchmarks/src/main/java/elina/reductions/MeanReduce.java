package elina.reductions;

import core.collective.AbstractReduction;


public class MeanReduce extends AbstractReduction<Double> {

	protected int length;
	
	public MeanReduce(int length) {
		this.length = length;
	}

	
	public Double reduce(Double[] array) {
		double result = 0.0;
		for(int i = 0; i < array.length; i++)
			result += array[i];
		return result/length;
	}	
}

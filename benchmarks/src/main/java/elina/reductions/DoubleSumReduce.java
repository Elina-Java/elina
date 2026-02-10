package elina.reductions;


import core.collective.AbstractReduction;


public class DoubleSumReduce extends AbstractReduction<Double>{

	public Double reduce(Double[] results) {
		double result = 0;
		
		for (int i = 0; i < results.length; i++) {
			result += results[i];
		}
		return result;
	}

}

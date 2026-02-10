package javaGrande.elina.cluster.series;

import core.collective.AbstractReduction;

public class SeriesReduction extends AbstractReduction<SeriesResult>{

	private final double[][] TestArray;

	public SeriesReduction(SeriesInput size) {
		this.TestArray = new double[2][size.size];
	}
	

	@Override
	public SeriesResult reduce(final SeriesResult[] data) {
		SeriesResult result = new SeriesResult(TestArray, Integer.MAX_VALUE);
	
		for (int k = 0; k< data.length; k++)  {
			SeriesResult seriesResult = data[k];
			
			for (int i = 0; i < 2; i++) 
				for (int j = 0; j < seriesResult.result[i].length; j++) 
					result.result[i][j+seriesResult.pos] = seriesResult.result[i][j];
		
			result.pos = Math.min(result.pos, seriesResult.pos);
		}
		
		return result;
	}

}

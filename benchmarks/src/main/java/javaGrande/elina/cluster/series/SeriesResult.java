package javaGrande.elina.cluster.series;

import java.io.Serializable;

public class SeriesResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	final double[][] result;
	int pos;
			
	public SeriesResult(SeriesInput size) {
		this(new double[2][size.size], size.pos);
	}
	
	
	public SeriesResult(double[][] result, int pos) {
		this.result = result;
		this.pos = pos;
	}
}

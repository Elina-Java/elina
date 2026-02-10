package javaGrande.elina.cluster.series;

import java.io.Serializable;

public class SeriesInput implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int size;
	int pos;
	
	public SeriesInput(int i) {
		this.size=i;
	}

	public SeriesInput(int slice, int xi) {
		this(slice);
		this.pos=xi;
	}
}

package javaGrande.elina.multicore.montecarlo;

import java.util.List;

import core.collective.AbstractReduction;

public class AppDemoRed extends AbstractReduction<List<ToResult>> {

	public List<ToResult> reduce(List<ToResult>[] data) {
		
		for (int i = 1; i < data.length; i++) 
			data[0].addAll(data[i]);
		
		
		return data[0];
	}

}

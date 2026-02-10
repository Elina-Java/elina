package javaGrande.elina.multicore.montecarlo;

import java.io.Serializable;

public class ToTask implements Serializable {

	private String header;
	private long randomSeed;

	public ToTask(String header, long randomSeed) {
		this.header = header;
		this.randomSeed = randomSeed;
	}

	public String get_header() {
		return (this.header);
	}

	public long get_randomSeed() {
		return (this.randomSeed);
	}

}

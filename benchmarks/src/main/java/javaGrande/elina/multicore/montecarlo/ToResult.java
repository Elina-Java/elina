package javaGrande.elina.multicore.montecarlo;

import java.io.Serializable;

public class ToResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private String header;
	private double expectedReturnRate = Double.NaN;
	private double volatility = Double.NaN;
	
	private double[] pathValue;

	public ToResult(String header, double expectedReturnRate,
			double volatility, double volatility2, double finalStockPrice,
			double[] pathValue) {
		this.header = header;
		this.expectedReturnRate = expectedReturnRate;
		this.volatility = volatility;

		this.pathValue = pathValue;
	}

	public String toString() {
		return (header);
	}

	public double get_expectedReturnRate() {
		return (this.expectedReturnRate);
	}

	public double get_volatility() {
		return (this.volatility);
	}

	public double[] get_pathValue() {
		return (this.pathValue);
	}
}

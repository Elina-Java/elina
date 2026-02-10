package javaGrande.elina.multicore.montecarlo;

public class ReturnPath extends PathId {

	public final static int COMPOUNDED = 1;
	public final static int NONCOMPOUNDED = 2;

	private double[] pathValue;
	private int nPathValue = 0;
	private int returnDefinition = 0;
	private double expectedReturnRate = Double.NaN;
	private double volatility = Double.NaN;
	private double volatility2 = Double.NaN;
	private double mean = Double.NaN;
	private double variance = Double.NaN;

	public ReturnPath(double[] pathValue, int nPathValue, int returnDefinition) {
		this.pathValue = pathValue;
		this.nPathValue = nPathValue;
		this.returnDefinition = returnDefinition;
	}


	public int get_returnDefinition() throws DemoException {
		if (this.returnDefinition == 0)
			throw new DemoException("Variable returnDefinition is undefined!");
		return (this.returnDefinition);
	}


	public double get_expectedReturnRate() throws DemoException {
		if (this.expectedReturnRate == Double.NaN)
			throw new DemoException("Variable expectedReturnRate is undefined!");
		return (this.expectedReturnRate);
	}


	public double get_volatility() throws DemoException {
		if (this.volatility == Double.NaN)
			throw new DemoException("Variable volatility is undefined!");
		return (this.volatility);
	}


	public double get_volatility2() throws DemoException {
		if (this.volatility2 == Double.NaN)
			throw new DemoException("Variable volatility2 is undefined!");
		return (this.volatility2);
	}





	private void computeExpectedReturnRate() throws DemoException {
		this.expectedReturnRate = mean / get_dTime() + 0.5 * volatility2;
	}

	private void computeVolatility() throws DemoException {
		if (this.variance == Double.NaN)
			throw new DemoException("Variable variance is not defined!");
		this.volatility2 = variance / get_dTime();
		this.volatility = Math.sqrt(volatility2);
	}

	private void computeMean() throws DemoException {
		if (this.nPathValue == 0)
			throw new DemoException("Variable nPathValue is undefined!");
		this.mean = 0.0;
		for (int i = 1; i < nPathValue; i++) {
			mean += pathValue[i];
		}
		this.mean /= ((double) (nPathValue - 1.0));
	}

	private void computeVariance() throws DemoException {
		if (this.mean == Double.NaN || this.nPathValue == 0)
			throw new DemoException(
					"Variable mean and/or nPathValue are undefined!");
		this.variance = 0.0;
		for (int i = 1; i < nPathValue; i++) {
			variance += (pathValue[i] - mean) * (pathValue[i] - mean);
		}
		this.variance /= ((double) (nPathValue - 1.0));
	}

	public void estimatePath() throws DemoException {
		computeMean();
		computeVariance();
		computeExpectedReturnRate();
		computeVolatility();
	}
}

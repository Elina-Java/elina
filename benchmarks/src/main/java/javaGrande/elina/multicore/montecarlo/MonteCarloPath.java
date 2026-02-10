package javaGrande.elina.multicore.montecarlo;

import java.util.Random;

public class MonteCarloPath extends PathId {

	
	
	private double[] fluctuations;
	private double[] pathValue;
	private int returnDefinition = 0;
	private double expectedReturnRate = Double.NaN;
	private double volatility = Double.NaN;
	private int nTimeSteps = 0;
	@SuppressWarnings("unused")
	private double pathStartValue = Double.NaN;

	public MonteCarloPath() {
	}

	public MonteCarloPath(ReturnPath returnPath, int nTimeSteps)
			throws DemoException {
		/**
		 * These instance variables are members of PathId class.
		 */
		copyInstanceVariables(returnPath);
		this.nTimeSteps = nTimeSteps;
		this.pathValue = new double[nTimeSteps];
		this.fluctuations = new double[nTimeSteps];
		/**
		 * Whether to debug, and how.
		 */
	}

	public MonteCarloPath(PathId pathId, int returnDefinition,
			double expectedReturnRate, double volatility, int nTimeSteps)
			throws DemoException {
		/**
		 * These instance variables are members of PathId class. Invoking with
		 * this particular signature should point to the definition in the
		 * PathId class.
		 */
		copyInstanceVariables(pathId);
		this.returnDefinition = returnDefinition;
		this.expectedReturnRate = expectedReturnRate;
		this.volatility = volatility;
		this.nTimeSteps = nTimeSteps;
		this.pathValue = new double[nTimeSteps];
		this.fluctuations = new double[nTimeSteps];
		/**
		 * Whether to debug, and how.
		 */
	}


	public MonteCarloPath(String name, int startDate, int endDate,
			double dTime, int returnDefinition, double expectedReturnRate,
			double volatility, int nTimeSteps) {
		/**
		 * These instance variables are members of PathId class.
		 */
		set_name(name);
		set_startDate(startDate);
		set_endDate(endDate);
		set_dTime(dTime);
		this.returnDefinition = returnDefinition;
		this.expectedReturnRate = expectedReturnRate;
		this.volatility = volatility;
		this.nTimeSteps = nTimeSteps;
		this.pathValue = new double[nTimeSteps];
		this.fluctuations = new double[nTimeSteps];
		/**
		 * Whether to debug, and how.
		 */
	}



	/**
	 * Set method for private instance variable <code>fluctuations</code>.
	 * 
	 * @param fluctuations
	 *            the value to set for the instance variable
	 *            <code>fluctuations</code>.
	 */
	public void set_fluctuations(double[] fluctuations) {
		this.fluctuations = fluctuations;
	}

	/**
	 * Accessor method for private instance variable <code>pathValue</code>.
	 * 
	 * @return Value of instance variable <code>pathValue</code>.
	 * @exception DemoException
	 *                thrown if instance variable <code>pathValue</code> is
	 *                undefined.
	 */
	public double[] get_pathValue() throws DemoException {
		if (this.pathValue == null)
			throw new DemoException("Variable pathValue is undefined!");
		return (this.pathValue);
	}

	/**
	 * Set method for private instance variable <code>pathValue</code>.
	 * 
	 * @param pathValue
	 *            the value to set for the instance variable
	 *            <code>pathValue</code>.
	 */
	public void set_pathValue(double[] pathValue) {
		this.pathValue = pathValue;
	}

	

	/**
	 * Set method for private instance variable <code>returnDefinition</code>.
	 * 
	 * @param returnDefinition
	 *            the value to set for the instance variable
	 *            <code>returnDefinition</code>.
	 */
	public void set_returnDefinition(int returnDefinition) {
		this.returnDefinition = returnDefinition;
	}

	

	/**
	 * Set method for private instance variable <code>expectedReturnRate</code>.
	 * 
	 * @param expectedReturnRate
	 *            the value to set for the instance variable
	 *            <code>expectedReturnRate</code>.
	 */
	public void set_expectedReturnRate(double expectedReturnRate) {
		this.expectedReturnRate = expectedReturnRate;
	}

	

	/**
	 * Set method for private instance variable <code>volatility</code>.
	 * 
	 * @param volatility
	 *            the value to set for the instance variable
	 *            <code>volatility</code>.
	 */
	public void set_volatility(double volatility) {
		this.volatility = volatility;
	}

	/**
	 * Accessor method for private instance variable <code>nTimeSteps</code>.
	 * 
	 * @return Value of instance variable <code>nTimeSteps</code>.
	 * @exception DemoException
	 *                thrown if instance variable <code>nTimeSteps</code> is
	 *                undefined.
	 */
	public int get_nTimeSteps() throws DemoException {
		if (this.nTimeSteps == 0)
			throw new DemoException("Variable nTimeSteps is undefined!");
		return (this.nTimeSteps);
	}

	/**
	 * Set method for private instance variable <code>nTimeSteps</code>.
	 * 
	 * @param nTimeSteps
	 *            the value to set for the instance variable
	 *            <code>nTimeSteps</code>.
	 */
	public void set_nTimeSteps(int nTimeSteps) {
		this.nTimeSteps = nTimeSteps;
	}

	

	/**
	 * Set method for private instance variable <code>pathStartValue</code>.
	 * 
	 * @param pathStartValue
	 *            the value to set for the instance variable
	 *            <code>pathStartValue</code>.
	 */
	public void set_pathStartValue(double pathStartValue) {
		this.pathStartValue = pathStartValue;
	}

	// ------------------------------------------------------------------------
	/**
	 * Method for copying the suitable instance variable from a
	 * <code>ReturnPath</code> object.
	 * 
	 * @param obj
	 *            Object used to define the instance variables which should be
	 *            carried over to this object.
	 * @exception DemoException
	 *                thrown if there is a problem accessing the instance
	 *                variables from the target objetct.
	 */
	private void copyInstanceVariables(ReturnPath obj) throws DemoException {
		//
		// Instance variables defined in the PathId object.
		set_name(obj.get_name());
		set_startDate(obj.get_startDate());
		set_endDate(obj.get_endDate());
		set_dTime(obj.get_dTime());
		//
		// Instance variables defined in this object.
		this.returnDefinition = obj.get_returnDefinition();
		this.expectedReturnRate = obj.get_expectedReturnRate();
		this.volatility = obj.get_volatility();
	}

	


	/**
	 * Method for calculating the sequence of fluctuations, based around a
	 * Gaussian distribution of given mean and variance, as defined in this
	 * class' instance variables. Mapping from Gaussian distribution of (0,1) to
	 * (mean-drift,volatility) is done via Ito's lemma on the log of the stock
	 * price.
	 * 
	 * @param randomSeed
	 *            The psuedo-random number seed value, to start off a given
	 *            sequence of Gaussian fluctuations.
	 * @exception DemoException
	 *                thrown if there are any problems with the computation.
	 */
	public void computeFluctuationsGaussian(long randomSeed)
			throws DemoException {
		if (nTimeSteps > fluctuations.length)
			throw new DemoException(
					"Number of timesteps requested is greater than the allocated array!");
		//
		// First, make use of the passed in seed value.
		Random rnd;
		if (randomSeed == -1) {
			rnd = new Random();
		} else {
			rnd = new Random(randomSeed);
		}
		//
		// Determine the mean and standard-deviation, from the mean-drift and
		// volatility.
		double mean = (expectedReturnRate - 0.5 * volatility * volatility)
				* get_dTime();
		double sd = volatility * Math.sqrt(get_dTime());
		double gauss;//, meanGauss = 0.0, variance = 0.0;
		for (int i = 0; i < nTimeSteps; i++) {
			gauss = rnd.nextGaussian();
//			meanGauss += gauss;
//			variance += (gauss * gauss);
			//
			// Now map this onto a general Gaussian of given mean and variance.
			fluctuations[i] = mean + sd * gauss;
			// dbgPrintln("gauss="+gauss+" fluctuations="+fluctuations[i]);
		}
//		meanGauss /= (double) nTimeSteps;
//		variance /= (double) nTimeSteps;
		// dbgPrintln("meanGauss="+meanGauss+" variance="+variance);
	}

	
	/**
	 * Method for calculating the corresponding rate path, given the
	 * fluctuations and starting rate value.
	 * 
	 * @param startValue
	 *            the starting value of the rate path, to be updated with the
	 *            precomputed fluctuations.
	 * @exception DemoException
	 *                thrown if there are any problems with the computation.
	 */
	public void computePathValue(double startValue) throws DemoException {
		pathValue[0] = startValue;
		if (returnDefinition == ReturnPath.COMPOUNDED
				|| returnDefinition == ReturnPath.NONCOMPOUNDED) {
			for (int i = 1; i < nTimeSteps; i++) {
				pathValue[i] = pathValue[i - 1] * Math.exp(fluctuations[i]);
			}
		} else {
			throw new DemoException("Unknown or undefined update method.");
		}
	}
}

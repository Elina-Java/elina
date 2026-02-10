package javaGrande.elina.multicore.montecarlo;

import java.io.Serializable;

public class ToInitAllTasks implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private int startDate;
	private int endDate;
	private double dTime;
	private int returnDefinition;
	private double expectedReturnRate;
	private double volatility;
	private int nTimeSteps;
	private double pathStartValue;

	public ToInitAllTasks(String header, String name, int startDate,
			int endDate, double dTime, int returnDefinition,
			double expectedReturnRate, double volatility, double pathStartValue) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dTime = dTime;
		this.returnDefinition = returnDefinition;
		this.expectedReturnRate = expectedReturnRate;
		this.volatility = volatility;
		this.pathStartValue = pathStartValue;
	}

	public ToInitAllTasks(ReturnPath obj, int nTimeSteps, double pathStartValue)
			throws DemoException {
		//
		// Instance variables defined in the PathId object.
		this.name = obj.get_name();
		this.startDate = obj.get_startDate();
		this.endDate = obj.get_endDate();
		this.dTime = obj.get_dTime();

		this.returnDefinition = obj.get_returnDefinition();
		this.expectedReturnRate = obj.get_expectedReturnRate();
		this.volatility = obj.get_volatility();
		this.nTimeSteps = nTimeSteps;
		this.pathStartValue = pathStartValue;
	}



	public String get_name() {
		return (this.name);
	}


	public int get_startDate() {
		return (this.startDate);
	}


	public int get_endDate() {
		return (this.endDate);
	}


	public double get_dTime() {
		return (this.dTime);
	}


	public int get_returnDefinition() {
		return (this.returnDefinition);
	}


	public double get_expectedReturnRate() {
		return (this.expectedReturnRate);
	}


	public double get_volatility() {
		return (this.volatility);
	}


	public int get_nTimeSteps() {
		return (this.nTimeSteps);
	}


	public double get_pathStartValue() {
		return (this.pathStartValue);
	}
}

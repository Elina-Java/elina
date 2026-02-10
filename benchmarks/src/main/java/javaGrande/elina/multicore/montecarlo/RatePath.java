package javaGrande.elina.multicore.montecarlo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class RatePath extends PathId {
	
	public static final int DATUMFIELD = 4;
	private static final int MINIMUMDATE = 19000101;
	private static final double EPSILON = 10.0 * Double.MIN_VALUE;

	
	private double[] pathValue;
	private int[] pathDate;
	private int nAcceptedPathValue = 0;

	
	

	public RatePath(String filename) throws DemoException {
		readRatesFile(null, filename);
	}

	
	public RatePath(double[] pathValue, String name, int startDate,
			int endDate, double dTime) {
		set_name(name);
		set_startDate(startDate);
		set_endDate(endDate);
		set_dTime(dTime);
		
		this.pathValue = pathValue;
		this.nAcceptedPathValue = pathValue.length;
	}

	public RatePath(MonteCarloPath mc) throws DemoException {
		//
		// Fields pertaining to the parent PathId object:
		set_name(mc.get_name());
		set_startDate(mc.get_startDate());
		set_endDate(mc.get_endDate());
		set_dTime(mc.get_dTime());
		//
		// Fields pertaining to RatePath object itself.
		pathValue = mc.get_pathValue();
		nAcceptedPathValue = mc.get_nTimeSteps();
		//
		// Note that currently the pathDate is neither declared, defined,
		// nor used in the MonteCarloPath object.
		pathDate = new int[nAcceptedPathValue];
	}

	
	public RatePath(int pathValueLength, String name, int startDate,
			int endDate, double dTime) {
		set_name(name);
		set_startDate(startDate);
		set_endDate(endDate);
		set_dTime(dTime);
		this.pathValue = new double[pathValueLength];
		this.nAcceptedPathValue = pathValue.length;
	}

	
	public void inc_pathValue(double[] operandPath) throws DemoException {
		if (pathValue.length != operandPath.length)
			throw new DemoException(
					"The path to update has a different size to the path to update with!");
		for (int i = 0; i < pathValue.length; i++)
			pathValue[i] += operandPath[i];
	}

	public void inc_pathValue(double scale) throws DemoException {
		if (pathValue == null)
			throw new DemoException("Variable pathValue is undefined!");
		for (int i = 0; i < pathValue.length; i++)
			pathValue[i] *= scale;
	}

	

	public double getEndPathValue() {
		return (getPathValue(pathValue.length - 1));
	}

	private double getPathValue(int index) {
		return (pathValue[index]);
	}

	public ReturnPath getReturnCompounded() throws DemoException {
		if (pathValue == null || nAcceptedPathValue == 0) {
			throw new DemoException("The Rate Path has not been defined!");
		}
		double[] returnPathValue = new double[nAcceptedPathValue];
		returnPathValue[0] = 0.0;
		try {
			for (int i = 1; i < nAcceptedPathValue; i++) {
				returnPathValue[i] = Math.log(pathValue[i] / pathValue[i - 1]);
			}
		} catch (ArithmeticException aex) {
			throw new DemoException("Error in getReturnLogarithm:"
					+ aex.toString());
		}
		ReturnPath rPath = new ReturnPath(returnPathValue, nAcceptedPathValue,
				ReturnPath.COMPOUNDED);
		//
		// Copy the PathId information to the ReturnPath object.
		rPath.copyInstanceVariables(this);
		rPath.estimatePath();
		return (rPath);
	}


	
	private void readRatesFile(String dirName, String filename)
			throws DemoException {
		java.io.File ratesFile = new File(dirName, filename);
		java.io.BufferedReader in;
		if (!ratesFile.canRead()) {
			throw new DemoException("Cannot read the file "
					+ ratesFile.toString());
		}
		try {
			in = new BufferedReader(new FileReader(ratesFile));
		} catch (FileNotFoundException fnfex) {
			throw new DemoException(fnfex.toString());
		}
		//
		// Proceed to read all the lines of data into a Vector object.
		int iLine = 0, initNlines = 100, nLines = 0;

		String aLine;
		Vector<String> allLines = new Vector<String>(initNlines);
		try {
			while ((aLine = in.readLine()) != null) {
				iLine++;
				//
				// Note, I'm not entirely sure whether the object passed in is
				// copied
				// by value, or just its reference.
				allLines.addElement(aLine);
			}
		} catch (IOException ioex) {
			throw new DemoException("Problem reading data from the file "
					+ ioex.toString());
		}
		nLines = iLine;
		//
		// Now create an array to store the rates data.
		this.pathValue = new double[nLines];
		this.pathDate = new int[nLines];
		nAcceptedPathValue = 0;
		iLine = 0;
		for (Enumeration<String> enu = allLines.elements(); enu
				.hasMoreElements();) {
			aLine = enu.nextElement();
			String[] field = Utilities.splitString(",", aLine);
			int aDate = Integer.parseInt("19" + field[0]);
			//
			// static double Double.parseDouble() method is a feature of JDK1.2!
			double aPathValue = Double.valueOf(field[DATUMFIELD]).doubleValue();
			if ((aDate <= MINIMUMDATE) || (Math.abs(aPathValue) < EPSILON)) {
				System.err.println("Skipped erroneous data in " + filename
						+ " indexed by date=" + field[0] + ".");
			} else {
				pathDate[iLine] = aDate;
				pathValue[iLine] = aPathValue;
				iLine++;
			}
		}
		//
		// Record the actual number of accepted data points.
		nAcceptedPathValue = iLine;
		//
		// Now to fill in the structures from the 'PathId' class.
		set_name(ratesFile.getName());
		set_startDate(pathDate[0]);
		set_endDate(pathDate[nAcceptedPathValue - 1]);
		set_dTime((double) (1.0 / 365.0));
	}
}

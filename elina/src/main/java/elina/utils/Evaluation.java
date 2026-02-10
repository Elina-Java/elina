package elina.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import core.Level;
import drivers.Adapters;
import drivers.HierarchyLevel;

/**
 * A class for measuring execution times
 *  
 * @author Joao Saramago, Herve Paulino
 *
 */
public class Evaluation {

	/**
	 * Constant that denotes the number of nanoseconds per second
	 */
	private final static long NANOSECONDS_PER_SECOND = 1000000000;

	public static boolean writeFile = true;

	/**
	 * Number of measurements to sample
	 */
	private int numberOfMeasurements = 0;

	/**
	 * The last measurement
	 */
	private long measurement;

	/**
	 * A queue with all measurements performed so far
	 */
	private final PriorityQueue<Long> measurements; 

	/**
	 * The output stream where the measurements are written to
	 */
	private final PrintStream out;

	private String appName;


	public Evaluation(String fileName, int EXECS) throws IOException {
		this(EXECS, new PrintStream(new FileOutputStream(fileName)));

	}

	public Evaluation(int EXECS)  {
		this(EXECS, System.out);

	}

	private Evaluation(int EXECS, PrintStream out)  {
		this.out = out;
		//	this.numberOfMeasurements = EXECS;
		this.measurements = EXECS == 0 ? new PriorityQueue<Long>() : new PriorityQueue<Long>(EXECS);
	}


	public void writeAppName(String appName) throws IOException {
		this.out.print(appName+"; ");
	}

	public void writeAppName(String appName, int classe) throws IOException {
		writeAppName(appName,""+classe);
	}

	public void writeAppName(String appName, String classe) throws IOException {
		if(PartitionedTimer.appName==null)
			PartitionedTimer.appName = appName+"."+classe;
		if(this.appName==null)
			this.appName = appName+"."+classe;
		this.out.print(appName+"."+classe+"; ");
	}


	public void writeTime() throws IOException {
		this.out.print((this.measurement/(double)NANOSECONDS_PER_SECOND) + "; ");
	}

	public void startTimer() {
		this.measurement = System.nanoTime();
	}

	public void stopTimer() {
		this.measurement = System.nanoTime() - this.measurement;
		this.measurements.add(this.measurement);
		this.numberOfMeasurements++;
	}

	/**
	 * Issues a newline and closes the output stream, if it is not the standard output
	 * @throws IOException
	 */
	public void end() throws IOException {
		this.out.println();
		out.flush();
		if (out != System.out)
			out.close();
	}

	public String toString() {
		return this.measurement + "ns (" + this.measurement/NANOSECONDS_PER_SECOND + "s)";
	}

	/**
	 * 
	 * @return AVG in seconds
	 */
	public double getAverage()
	{
		int waste = numberOfMeasurements / 3;
		ArrayList<Long> elems = new ArrayList<Long>(this.measurements);
		Collections.sort(elems);
		Long[] m = elems.toArray(new Long[this.measurements.size()]);

		double[] values = new double[numberOfMeasurements-2*waste];
		for (int i = waste; i < (numberOfMeasurements - waste); i++)
			values[i-waste] = m[i];

		return average(values)/NANOSECONDS_PER_SECOND;
	}

	/**
	 * 
	 * @return STDV in seconds
	 */
	public double getSTDV()
	{
		int waste = numberOfMeasurements / 3;
		ArrayList<Long> elems = new ArrayList<Long>(this.measurements);
		Collections.sort(elems);
		Long[] m = elems.toArray(new Long[this.measurements.size()]);

		double[] values = new double[numberOfMeasurements - 2*waste];
		for (int i = waste; i < (numberOfMeasurements - waste); i++)
			values[i-waste]=m[i];

		return Evaluation.standardDeviation(values, average(values))/NANOSECONDS_PER_SECOND;
	}

	public void writeAverage() throws IOException {
		this.out.print("; AVG; " + getAverage());
	}

	public void writeSTDV() {
		this.out.println(";\tSTDV; " + getSTDV());
		if(writeFile)
			writeCSVFile();
	}

	public void writeCSVFile() {
		String modifier = "";
		if(Adapters.getDomainDecompositionDriver(Level.Node)!=null)
		{
			if(Adapters.getDomainDecompositionDriver(Level.Node).toString().contains("Hier"))
			{
				if(PartitionedTimer.isFlatTuned)
					return;
				modifier = "H";
				if(Adapters.getSchedulingDriver().toString().contains("Sibling"))
					modifier+="SRR";
			}
			else
			{
				modifier = "F";
				if(PartitionedTimer.isFlatTuned)
					modifier += "Tuned";
				//não se deve fazer flat com roundrobin
				if(Adapters.getSchedulingDriver().toString().contains("Sibling"))
					return;
			}
		}
		else
			modifier = "S";

		try {
			String resultsDir = "./results/";
			File dir = new File(resultsDir);
			dir.mkdir();
			if(Adapters.getPartitioningDriver()!=null)
			{
				String tmp = Adapters.getPartitioningDriver().toString();
				resultsDir+=tmp.substring(tmp.indexOf(".")+1, tmp.indexOf("IterativePartitioner"));
				resultsDir+="."+Adapters.getHierarchyReadDriver().getHierarchyRoot().getLevel(HierarchyLevel.L1).size;
				resultsDir+="/";
			}
			else
			{
				resultsDir+="Sequential/";
			}
			dir = new File(resultsDir);
			dir.mkdir();
			FileOutputStream fos = new FileOutputStream(resultsDir+modifier+"-"+appName+".csv");
			PrintWriter pw = new PrintWriter(fos);
			pw.println(getAverage()+","+getSTDV());
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to write to file: "+e.toString());
			//e.printStackTrace();
		}
	}

	//Mathematical Operations

	public static double standardDeviation(double[] values, double avg)
	{
		double sum=0;
		for(int i=0;i<values.length;i++)
		{
			sum+=(values[i]-avg)*(values[i]-avg);
		}
		sum/=values.length;
		return Math.sqrt(sum);
	}

	public static double average(double[] values)
	{
		double sum = 0;
		for(int i=0;i<values.length;i++)
		{
			sum+=values[i];
		}
		return sum/values.length;
	}
}

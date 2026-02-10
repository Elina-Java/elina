package elina.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.Level;
import drivers.Adapters;
import drivers.HierarchyLevel;


public class PartitionedTimer {
	//private final static double NANOSECONDS_PER_SECOND = 1000000000;
	public static int NEXECS;
	public static String appName;

	public static Evaluation expansion;
	public static Evaluation contraction;
	public static Evaluation execution;
	public static Evaluation reduction;
	public static Evaluation affinity;
	public static int counter=0;
	public static Map<Integer,Integer> threadsTasks = new ConcurrentHashMap<Integer,Integer>();
	public static boolean isFlatTuned = false;

	public static void startAffinityMapping()
	{		
		if(affinity==null)
			affinity = new Evaluation(NEXECS);
		affinity.startTimer();
	}

	public static void startExpansion()
	{
		if(expansion==null)
			expansion = new Evaluation(NEXECS);
		expansion.startTimer();
	}
	
	public static void startContraction()
	{
		if(contraction==null)
			contraction = new Evaluation(NEXECS);
		contraction.startTimer();
	}

	public static void endExpansion()
	{
		expansion.stopTimer();
	}
	
	public static void endContraction()
	{
		contraction.stopTimer();
	}

	public static void endAffinityMapping()
	{
		affinity.stopTimer();
	}

	public static void startExecution()
	{
		if(execution==null)
			execution = new Evaluation(NEXECS);
		execution.startTimer();
	}

	public static void endExecution()
	{
		execution.stopTimer();
	}

	public static void startReduction()
	{
		if(reduction==null)
			reduction = new Evaluation(NEXECS);
		reduction.startTimer();
	}

	public static void endReduction()
	{
		reduction.stopTimer();
	}
	
	public static void printResults()
	{
		try {
			System.out.println("\n--[Results]--");
			System.out.print("AffinityMapping: ");
			affinity.writeTime();
			System.out.println();
			System.out.print("Expansion: ");
			expansion.writeTime();
			System.out.println();
			System.out.print("Contraction: ");
			contraction.writeTime();
			System.out.println();
			System.out.print("Execution: ");
			execution.writeTime();
			System.out.println();
			if(reduction!=null)
			{
				System.out.print("Reduction: ");
				reduction.writeTime();
				System.out.println();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		counter++;
		if(counter==NEXECS)
		{
			printTotalResults();
			writeCSVFile();
			writeTaskAssignments();
		}
	}

	private static void writeTaskAssignments() {
		String modifier = "";
		if(Adapters.getDomainDecompositionDriver(Level.Node).toString().contains("Hier"))
		{
			if(isFlatTuned)
				return;
			modifier = "H";
			if(Adapters.getSchedulingDriver().toString().contains("Sibling"))
				modifier+="SRR";
		}
		else
		{
			modifier = "F";
			if(isFlatTuned)
				modifier += "Tuned";
			//não se deve fazer flat com roundrobin
			if(Adapters.getSchedulingDriver().toString().contains("Sibling"))
				return;
		}
		
		try {
			String resultsDir = "./results/";
			File dir = new File(resultsDir);
			dir.mkdir();
			String tmp = Adapters.getPartitioningDriver().toString();
			resultsDir+=tmp.substring(tmp.indexOf(".")+1, tmp.indexOf("IterativePartitioner"));
			resultsDir+="."+Adapters.getHierarchyReadDriver().getHierarchyRoot().getLevel(HierarchyLevel.L1).size;
			resultsDir+="/";
			dir = new File(resultsDir);
			dir.mkdir();
			FileOutputStream fos = new FileOutputStream(resultsDir+modifier+"-"+"THREADS-TASKS"+"-"+appName+".csv");
			PrintWriter pw = new PrintWriter(fos);
			for(int i=1;i<Integer.MAX_VALUE;i++)
				if(threadsTasks.containsKey(i))
				{
					pw.print(i);
					if(threadsTasks.containsKey(i+1))
						pw.print(",");
				}
				else
					break;
			pw.println();
			for(int i=1;i<Integer.MAX_VALUE;i++)
				if(threadsTasks.containsKey(i))
				{
					pw.print(threadsTasks.get(i));
					if(threadsTasks.containsKey(i+1))
						pw.print(",");
				}
				else
					break;
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to write to file: "+e.toString());
			//e.printStackTrace();
		}
		Evaluation.writeFile=false;
	}

	public static void printTotalResults()
	{
		try {
			System.out.println("\n--[AVERAGE Results]--");
			System.out.print("AffinityMapping: ");
			System.out.print("AVG: "+affinity.getAverage());
			System.out.println("\tSTDV: "+affinity.getSTDV());
			
			System.out.print("Expansion: ");
			System.out.print("AVG: "+expansion.getAverage());
			System.out.println("\tSTDV: "+expansion.getSTDV());
			
			System.out.print("Contraction: ");
			System.out.print("AVG: "+contraction.getAverage());
			System.out.println("\tSTDV: "+contraction.getSTDV());
			
			System.out.print("Execution: ");
			System.out.print("AVG: "+execution.getAverage());
			System.out.println("\tSTDV: "+execution.getSTDV());
			
			if(reduction!=null)
			{
				System.out.print("Reduction: ");
				System.out.print("AVG: "+reduction.getAverage());
				System.out.println("\tSTDV: "+reduction.getSTDV());
			}
			System.out.println();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void writeCSVFile() {
		String modifier = "";
		if(Adapters.getDomainDecompositionDriver(Level.Node).toString().contains("Hier"))
		{
			if(isFlatTuned)
				return;
			modifier = "H";
			if(Adapters.getSchedulingDriver().toString().contains("Sibling"))
				modifier+="SRR";
		}
		else
		{
			modifier = "F";
			if(isFlatTuned)
				modifier += "Tuned";
			//não se deve fazer flat com roundrobin
			if(Adapters.getSchedulingDriver().toString().contains("Sibling"))
				return;
		}
		
		try {
			String resultsDir = "./results/";
			File dir = new File(resultsDir);
			dir.mkdir();
			String tmp = Adapters.getPartitioningDriver().toString();
			resultsDir+=tmp.substring(tmp.indexOf(".")+1, tmp.indexOf("IterativePartitioner"));
			resultsDir+="."+Adapters.getHierarchyReadDriver().getHierarchyRoot().getLevel(HierarchyLevel.L1).size;
			resultsDir+="/";
			dir = new File(resultsDir);
			dir.mkdir();
			FileOutputStream fos = new FileOutputStream(resultsDir+modifier+"-"+"Breakdown"+"-"+appName+".csv");
			PrintWriter pw = new PrintWriter(fos);
			pw.println(affinity.getAverage()+","+affinity.getSTDV());
			pw.println(expansion.getAverage()+","+expansion.getSTDV());
			pw.println(contraction.getAverage()+","+contraction.getSTDV());
			pw.println(execution.getAverage()+","+execution.getSTDV());
			if(reduction!=null)
				pw.println(reduction.getAverage()+","+reduction.getSTDV());
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to write to file: "+e.toString());
			//e.printStackTrace();
		}
		Evaluation.writeFile=false;
	}

	public static void setThreadTasks(int threadID, int nTasks) {
		threadsTasks.put(threadID, nTasks);
	}
}

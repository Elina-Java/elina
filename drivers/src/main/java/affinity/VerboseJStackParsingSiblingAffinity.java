package affinity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

import drivers.Adapters;
import drivers.AffinityMapperDriver;
import drivers.HierarchyLevel;
import drivers.HierarchyReadDriver;
import drivers.TaskExecutorDriver;

public class VerboseJStackParsingSiblingAffinity implements AffinityMapperDriver {
	private final static HierarchyReadDriver hierarchyReader = Adapters.getHierarchyReadDriver();
	
	//for caching
	private boolean done=false;

	@Override
	public void setAffinities(TaskExecutorDriver taskManager) {
		if(!done)
		{
			String s = ManagementFactory.getRuntimeMXBean().getName();
			int pid = Integer.parseInt(s.substring(0, s.indexOf("@")));
			
			System.out.println("JVM Process PID: "+pid);

			try 
			{
				Process p = Runtime.getRuntime().exec("jstack "+ pid);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()),2000);
				int[][] threadInfo = parseInfo(stdInput,taskManager.getNumberOfWorkers());

				HierarchyLevel root = hierarchyReader.getHierarchyRoot();
				int[][] siblings = root.getBottomUpFirstShared().getSiblings();

				System.out.println("---Mappings---");
				String zeros = "0x00000000000000000";
				int t=0;
				for(int group=0;group<siblings.length;group++)
				{

					String groupString="[";
					long groupMask=0;
					int groupSize = siblings[group].length;
					for(int i=0;i<groupSize;i++)
					{
						groupString+=siblings[group][i]+(i<groupSize-1 ? "," : "");
						double pow = Math.pow(2, siblings[group][i]);
						groupMask+=pow;
					}
					groupString+="]";
					
					String groupMaskS = Long.toHexString(groupMask);
					groupMaskS = zeros.substring(0,zeros.length()-groupMaskS.length())+groupMaskS;
					for(int i=0;i<groupSize;i++)
					{
						int tID = threadInfo[t][0];
						int nid = threadInfo[t][1];
						String command = "taskset -p "+ groupMaskS + " "+ threadInfo[t++][1];
						Runtime.getRuntime().exec(command);
						System.out.println("Thread-"+tID+" PID: "+nid+" Cores: "+groupString);
						System.out.println("Shell Command: "+command);
						System.out.println("Validation: " +verifyAffinity(nid,groupMaskS));
						System.out.println();
					}
				}
				stdInput.close();
			} 
			catch (IOException e) 
			{
				System.err.println("'jstack' is not in PATH or 'taskset' does not exist (OS is not Linux).");
			}
			done=true;
		}
	}
	
	private boolean verifyAffinity(int nid, String expected) throws IOException
	{
		String command = "taskset -p "+ nid;
		Process p = Runtime.getRuntime().exec(command);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()),2000);
		String line = stdInput.readLine();
		System.out.println(line);
		stdInput.close();
		String[] toks = line.split(":");
		String affinity = toks[1].substring(1,toks[1].length());
		long valAffinity = Long.parseLong(affinity,16);
		long expectedVal = Long.parseLong(expected.substring(2),16);
		return valAffinity==expectedVal;
	}

	private int[][] parseInfo(BufferedReader stdInput, int nthreads) throws IOException
	{
		int[][] threadInfo = new int[nthreads][2];
		String line = null;
		String tname = null;
		int nid=0;
		int tid=0;
		boolean found=false;
		
		System.out.println("---Task Executor Threads---");
		while ((line = stdInput.readLine()) != null)
		{
			if(line.length()>0 && line.substring(0,2).equals("\"T"))
			{
				found=false;
				String[] toks = line.split(" ");
				tname = toks[0];
				tid = Integer.valueOf(toks[0].substring(toks[0].indexOf('-')+1,toks[0].length()-1));
				nid = Integer.parseInt(toks[4].substring(6, toks[4].length()),16);
			}
			else if(!found && line.contains(".ArrayBlockingQueue."))
			{
				found=true;
				threadInfo[tid-3][0]=Integer.valueOf(tname.substring(tname.indexOf('-')+1, tname.length()-1));
				threadInfo[tid-3][1]=nid;
				System.out.println("Name: "+ tname.substring(1, tname.length()-1) + " PID: "+nid);
			}
		}
		return threadInfo;
	}
}

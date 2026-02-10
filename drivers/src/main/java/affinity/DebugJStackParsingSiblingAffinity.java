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
import elina.utils.PartitionedTimer;

public class DebugJStackParsingSiblingAffinity implements AffinityMapperDriver {
	private final static HierarchyReadDriver hierarchyReader = Adapters.getHierarchyReadDriver();
	
	//for caching
	private boolean done=false;

	@Override
	public void setAffinities(TaskExecutorDriver taskManager) {
		PartitionedTimer.startAffinityMapping();
		if(!done)
		{
			String s = ManagementFactory.getRuntimeMXBean().getName();
			int pid = Integer.parseInt(s.substring(0, s.indexOf("@")));

			try 
			{
				Process p = Runtime.getRuntime().exec("jstack "+ pid);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()),2000);
				int[][] threadInfo = parseInfo(stdInput,taskManager.getNumberOfWorkers());

				HierarchyLevel root = hierarchyReader.getHierarchyRoot();
				int[][] siblings = root.getBottomUpFirstShared().getSiblings();

				String zeros = "0x00000000000000000";
				int t=0;
				for(int group=0;group<siblings.length;group++)
				{

					long groupMask=0;
					int groupSize = siblings[group].length;
					for(int i=0;i<groupSize;i++)
					{
						double pow = Math.pow(2, siblings[group][i]);
						groupMask+=pow;
					}
					
					String groupMaskS = Long.toHexString(groupMask);
					groupMaskS = zeros.substring(0,zeros.length()-groupMaskS.length())+groupMaskS;
					for(int i=0;i<groupSize;i++)
					{
						String command = "taskset -p "+ groupMaskS + " "+ threadInfo[t++][1];
						Runtime.getRuntime().exec(command);
						//System.out.println(command);
					}
				}
			} 
			catch (IOException e) 
			{
				System.err.println("'jstack' is not in PATH or 'taskset' does not exist (OS is not Linux).");
				//e.printStackTrace();
			}
			done=true;
		}
		PartitionedTimer.endAffinityMapping();
	}

	private int[][] parseInfo(BufferedReader stdInput, int nthreads) throws IOException
	{
		int[][] threadInfo = new int[nthreads][2];
		int tcounter=0;
		String line = null;
		String tname = null;
		int nid=0;
		int tid=0;
		boolean found=false;
		
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
				tcounter++;
			}
		}
		return threadInfo;
	}

}

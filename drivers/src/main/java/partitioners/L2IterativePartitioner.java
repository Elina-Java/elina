package partitioners;

import service.SOMDTask;
import core.collective.Distribution;
import drivers.Adapters;
import drivers.HierarchyLevel;
import drivers.PartitioningDriver;

public class L2IterativePartitioner implements PartitioningDriver {
	private int nParts;
	private long l2Size;

	public L2IterativePartitioner()
	{
		nParts=-1;
	}

	@Override
	public int getNparts(Distribution<?>[] distrs, HierarchyLevel hierarchy,SOMDTask<?> task) {
		HierarchyLevel l2 = hierarchy.getLevel(HierarchyLevel.L2);
		l2Size = l2.getSize()/l2.siblings[0].length;

		for(nParts=Adapters.getTaskExecutor().getNumberOfWorkers();nParts<Integer.MAX_VALUE;nParts++)
		{
			long sum=0;
			boolean valid=true;
			for(int i=0;i<distrs.length;i++)
			{
				float lineSize = distrs[i].getAverageLineSize(nParts);
				if(lineSize==0)
				{
					valid=false;
					break;
				}
				int sizePart=Math.round(distrs[i].getAveragePartitionSize(nParts)*distrs[i].getElementSize());
				sum = sum + sizePart;
			}
			if(valid && (sum+task.getDynamicDataSize(nParts))<=l2Size)
				break;
		}
		return nParts;
	}

	public int getCriteriaSize()
	{
		return (int) l2Size;
	}
}

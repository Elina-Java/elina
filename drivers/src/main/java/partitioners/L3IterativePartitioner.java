package partitioners;

import service.SOMDTask;
import core.collective.Distribution;
import drivers.Adapters;
import drivers.HierarchyLevel;
import drivers.PartitioningDriver;

public class L3IterativePartitioner implements PartitioningDriver {
	private int nParts;
	private long l3Size;

	public L3IterativePartitioner()
	{
		nParts=-1;
	}

	//E se não couber nem a soma de uma unidade mínima de cada?
	//hipótese: utilizar a L2
	@Override
	public int getNparts(Distribution<?>[] distrs, HierarchyLevel hierarchy,SOMDTask<?> task) {
		HierarchyLevel l3 = hierarchy.getLevel(HierarchyLevel.L3);
		l3Size = l3.getSize()/l3.siblings[0].length;

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
			if(valid && (sum+task.getDynamicDataSize(nParts))<=l3Size)
				break;
		}
		return nParts;
	}

	public int getCriteriaSize()
	{
		return (int) l3Size;
	}
}

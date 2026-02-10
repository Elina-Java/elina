package partitioners;

import service.SOMDTask;
import core.collective.Distribution;
import drivers.Adapters;
import drivers.HierarchyLevel;
import drivers.PartitioningDriver;

public class L3LAIterativePartitioner implements PartitioningDriver {
	private int nParts;
	private long l1Size;

	public L3LAIterativePartitioner()
	{
		nParts=-1;
	}

	//E se não couber nem a soma de uma unidade mínima de cada?
	//hipótese: utilizar a L2
	@Override
	public int getNparts(Distribution<?>[] distrs, HierarchyLevel hierarchy,SOMDTask<?> task) {
		HierarchyLevel l1 = hierarchy.getLevel(HierarchyLevel.L3);
		l1Size = l1.getSize()/l1.siblings[0].length;
		int cacheLineSize = l1.getCacheLineSize();

		for(nParts=Adapters.getTaskExecutor().getNumberOfWorkers();nParts<Integer.MAX_VALUE;nParts++)
		{
			long sum=0;
			boolean valid=true;
			for(int i=0;i<distrs.length;i++)
			{
				float averageLineSizeInBytes = distrs[i].getAverageLineSize(nParts)*distrs[i].getElementSize();
				if(averageLineSizeInBytes==0)
				{
					valid=false;
					break;
				}
				float partitionSize = distrs[i].getAveragePartitionSize(nParts)*distrs[i].getElementSize();
				int nLines = Math.round(partitionSize/averageLineSizeInBytes);
				int lineSizeInBytes = Math.round(averageLineSizeInBytes);
				int realPartitionSize = cacheLineSize*nLines*
						((lineSizeInBytes/cacheLineSize) + (lineSizeInBytes%cacheLineSize > 0 ? 1 : 0));
				sum = sum + realPartitionSize;
			}
			if(valid && (sum+task.getDynamicDataSize(nParts))<=l1Size)
				break;
		}
		return nParts;
	}

	public int getCriteriaSize()
	{
		return (int) l1Size;
	}
}

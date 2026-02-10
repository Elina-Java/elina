package affinity;

import drivers.AffinityMapperDriver;
import drivers.TaskExecutorDriver;
import elina.utils.PartitionedTimer;

public class DebugAllAffinity implements AffinityMapperDriver{

	@Override
	public void setAffinities(TaskExecutorDriver taskManager) {
		PartitionedTimer.startAffinityMapping();
		//System.out.println("No mappings to be done.");
		PartitionedTimer.endAffinityMapping();
		return;
	}

}

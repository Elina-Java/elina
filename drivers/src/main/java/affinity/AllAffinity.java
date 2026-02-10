package affinity;

import drivers.AffinityMapperDriver;
import drivers.TaskExecutorDriver;

public class AllAffinity implements AffinityMapperDriver{

	@Override
	public void setAffinities(TaskExecutorDriver taskManager) {
		//System.out.println("No mappings to be done.");
		return;
	}

}

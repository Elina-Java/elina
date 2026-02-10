package drivers;

import core.collective.Distribution;
import service.SOMDTask;


public interface PartitioningDriver {

	/**
	 * Returns the appropriate number of partitions, using some algorithm.
	 * @return the number of partitions
	 */
	int getNparts(Distribution<?>[] distributions, HierarchyLevel hierarchy, SOMDTask<?> task) throws IllegalArgumentException;
	
	/**
	 * Returns the TCL size used during the algorithm's decisions.
	 * @return the TCL size used
	 */
	int getCriteriaSize();

}

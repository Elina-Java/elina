package domainDecomposition;

import service.SOMDTask;
import core.collective.Distribution;
import drivers.Adapters;
import drivers.DomainDecompositionDriver;
import drivers.HierarchyLevel;
import drivers.HierarchyReadDriver;
import elina.utils.PartitionedTimer;

public class DebugHierarchicalDomainDecomposer implements DomainDecompositionDriver {

	private final static HierarchyReadDriver hierarchyReader = Adapters.getHierarchyReadDriver();

	@Override
	public <R> Object[][] decompose(Distribution<?>[] distr, SOMDTask<R> task, int nWorkers) {

		PartitionedTimer.startExpansion();
		//Determine nParts
		HierarchyLevel root = hierarchyReader.getHierarchyRoot();
		int nParts = Math.max(nWorkers,this.getnParts(distr, root, task));

		Object[][] partitions = new Object[distr.length][];
		for(int d=0; d<distr.length; d++)
		{
			partitions[d]=distr[d].distribution(nParts);
		}

		return partitions;
	}

	private <R> int getnParts(Distribution<?>[] distrs, HierarchyLevel hierarchy, SOMDTask<R> task) {
		return Adapters.getPartitioningDriver().getNparts(distrs, hierarchy, task);
	}
}

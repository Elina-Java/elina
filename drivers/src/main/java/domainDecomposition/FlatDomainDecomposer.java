package domainDecomposition;

import service.SOMDTask;
import core.collective.Distribution;
import drivers.DomainDecompositionDriver;
import drivers.HierarchyLevel;

public class FlatDomainDecomposer implements DomainDecompositionDriver {
	
	private int nParts;

	@Override
	public <R> Object[][] decompose(Distribution<?>[] distr, SOMDTask<R> task, int nWorkers) {

		this.nParts=nWorkers;
		
		Object[][] partitions = new Object[distr.length][];
		for(int d=0; d<distr.length; d++)
		{
			partitions[d]=distr[d].distribution(nParts);
		}

		return partitions;
	}

	private <R> int getnParts(Distribution<?>[] distrs, HierarchyLevel hierarchy, SOMDTask<R> task) {
		return nParts;
	}
}

package drivers;

import core.collective.Distribution;
import service.SOMDTask;

public interface DomainDecompositionDriver {
	
	/**
	 * Decomposes the domains handled by the distributions stored in distr.
	 * @param distr array holding the distributions
	 * @param task task that will be executed
	 * @param nWorkers number of workers that will execute the task
	 * @return
	 */
	<R> Object[][] decompose(Distribution<?>[] distr, SOMDTask<R> task, int nWorkers);

}

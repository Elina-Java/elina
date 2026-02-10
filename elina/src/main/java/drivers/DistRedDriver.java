package drivers;

import core.collective.Distribution;
import core.collective.DistributionsCombination;
import core.collective.Reduction;
import service.IFuture;
import service.IService;
import service.SOMDTask;

/**
 * 
 * @author Diogo Mourão
 * @author João Saramago
 *
 * @param <T>
 * @param <P>
 * @param <R>
 */

public interface DistRedDriver<R> 
{	

	IFuture<R> getResult(Distribution<?>[] distr, Reduction<R> red, String method, Object[] params, int index, IService[] services);
	IFuture<R> getResult(Distribution<?>[] distr, Reduction<R> red, String method, Object[] params, int index, IService service);
	
	IFuture<R> getResult(Distribution<?>[] distr, Reduction<R> red, SOMDTask<R> task, DistributionsCombination comb);
}
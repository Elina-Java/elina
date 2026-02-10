package core.collective;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import core.Level;
import drivers.Adapters;
import drivers.AffinityMapperDriver;
import drivers.DomainDecompositionDriver;
import drivers.SchedulingDriver;
import service.IFuture;
import service.IService;
import service.SOMDTask;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * 
 * Classe que delega a implementação do módulo de distribuição e redução para o
 * driver apropriado.
 * 
 * @author Diogo Mourão
 * @author João Saramago
 * 
 * @param <T>
 *            Tipo dos dados de entrada da Distribuição.
 * @param <P>
 *            Tipo dos dados de saida da Distribuição e consequentemente tipo de
 *            dados de entrada da Redução.
 * @param <R>
 *            Tipo do resultado final.
 */
public class SOMDExecutor {

	/**
	 * Execute a task accordingly to the SOMD execution model
	 * 
	 * @param distr
	 *            Distribution
	 * @param red
	 *            Reduçtion
	 * @param task
	 *            Task to execute
	 * @param service
	 *            Target service
	 * @return Computed result
	 */
	public <R> IFuture<R> getResult(Distribution<?>[] distr,
			Reduction<R> red, String method, Object[] params, int index,
			IService service) {
		
		//DistRedDriver<R> distRed = Adapters.getDistRedDriver(service.getLevel());

		//return distRed.getResult(distr, red, method, params, index, service);
		return null;
	}

	/**
	 * Execute a task accordingly to the SOMD execution model across multiple services
	 * 
	 * @param distr
	 *            Distribution
	 * @param red
	 *            Reduçtion
	 * @param task
	 *            Task to execute
	 * @param services
	 *            Target service instances
	 * @param level
	 *            Level of the service
	 * @return Computed result
	 */
	/*public <T, R> IFuture<R> getResult(Distribution<?>[] distr,
			Reduction<R> red, String method, Object[][] params, int index,
			IService[] services, Level level) {

		DomainDecompositionDriver decomposition = Adapters.getDomainDecompositionDriver(level);
		Object[][] partitions = decomposition.decompose(distr, task, nWorkers);
		
		
		return decomposition.getResult(distr, red, method, params, index, services);
	}*/

	
	@SuppressWarnings({ "unchecked", "restriction" })
	public <T, R> IFuture<R> getResult(Distribution<?>[] distr,
			Reduction<R> red, SOMDTask<R> task, DistributionsCombination combs, Level level) {
				
		SchedulingDriver<R> scheduler = Adapters.getSchedulingDriver();
		DomainDecompositionDriver decomposition = Adapters.getDomainDecompositionDriver(level);
		AffinityMapperDriver affinity = Adapters.getAffinityMapper();
		
		//Affinity Mapping
		affinity.setAffinities(Adapters.getTaskExecutor());
		
		int nWorkers = Adapters.getTaskExecutor().getNumberOfWorkers();
		Object[][] partitions = decomposition.decompose(distr, task, nWorkers);
		int nParts;
		int[][] combinations = null;
		
		if(combs != null)
		{
			combinations = combs.getCombinations();
			nParts = combinations.length;
		}
		else
		{
			//In the case that the distribution alters the imposed nparts
			//E.g. Matmult, which finds the first integer with an integer sqrt
			nParts=partitions[0].length;
		}
		
		Class<R> clazz;
		Type redType = ((ParameterizedType) task.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		try {
			clazz = (Class<R>) redType;
		}
		catch (java.lang.ClassCastException e) {
			clazz = (Class<R>) ((ParameterizedTypeImpl) redType).getRawType();
		}

		R[] results = (R[]) Array.newInstance(clazz, nParts);
		
		return scheduler.schedule(task, partitions, combinations, red, results, nWorkers);
	//	return distRed.getResult(distr, red, task, combs);
	}
}

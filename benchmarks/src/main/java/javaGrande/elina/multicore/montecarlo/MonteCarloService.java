package javaGrande.elina.multicore.montecarlo;



import java.util.List;

import service.IService;


public interface MonteCarloService extends IService {


	List<ToResult> runThread(int[] nRunMC, ToInitAllTasks initAllTasks);
}

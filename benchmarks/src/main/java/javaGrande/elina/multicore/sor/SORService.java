package javaGrande.elina.multicore.sor;

import service.IService;

public interface SORService extends IService {

	void getSolution(double omega, double [][] g, int n_iters);
	
}

package javaGrande.elina.multicore.lufact;

import service.IService;

public interface LUService extends IService{

	void lufact(double a[][], int lda, int n, int ipvt[]);
}

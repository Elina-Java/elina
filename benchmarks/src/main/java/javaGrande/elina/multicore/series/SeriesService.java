package javaGrande.elina.multicore.series;

import instrumentation.definitions.DistRed;
import instrumentation.definitions.DistRedTask;
import service.IService;

@DistRed
public interface SeriesService extends IService{

	@DistRedTask
	double[][] getFourierCoefficients(double[][]  TestArray, int size);
}

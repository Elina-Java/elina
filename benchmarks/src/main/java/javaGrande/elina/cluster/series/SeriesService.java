package javaGrande.elina.cluster.series;

import instrumentation.definitions.DistRed;
import instrumentation.definitions.DistRedTask;
import service.IService;

@DistRed
public interface SeriesService extends IService{

	@DistRedTask
	SeriesResult getFourierCoefficients(SeriesInput size);
}

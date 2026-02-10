package simulations.md;

import service.IService;

public interface MDService extends IService {
	
	void accel(Float3[] pos1, Float3[] pos2, float[] mass1, float[] mass2,
			float blockIndex1, float blockIndex2, float blockCount, Float3[] accel);
	
}
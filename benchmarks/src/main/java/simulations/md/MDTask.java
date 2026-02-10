package simulations.md;

import service.SOMDTask;

public class MDTask extends SOMDTask<float[][]> {

	private static final long serialVersionUID = 1L;

	private Float3[] pos1, pos2, accel;
	private float[]  mass1, mass2;
	private float blockIndex1, blockIndex2, blockCount;

	public MDTask(Float3[] pos1, Float3[] pos2, float[] mass1, float[] mass2,
			float blockIndex1, float blockIndex2, float blockCount, Float3[] accel) {
		this.pos1=pos1;
		this.pos2=pos2;
		this.mass1=mass1;
		this.mass2=mass2;
		this.blockIndex1=blockIndex1;
		this.blockIndex2=blockIndex2;
		this.blockCount=blockCount;
		this.accel=accel;
	}

	@Override
	public float[][] call(Object[] partition) {
		int iBeginA = ((int[]) partition[0])[0];
		int iEndA = ((int[]) partition[0])[1];
		
		blockIndex1=blockIndex1*blockCount;
		blockCount=blockCount*(pos1.length/(iEndA-iBeginA));
		

		float EPS = 1.0f;
		Float3 r = new Float3();
		for(int J=0;J<pos1.length/(iEndA-iBeginA);J++)
		{
			
			blockIndex2=blockIndex2*blockCount + J;
			if ( blockIndex1 == blockIndex2 )
				for ( int i = iBeginA; i < iEndA; ++i )
					for (int j = i + 1; j < iEndA; ++j )
					{			
						r.x = pos1[i].x - pos2[j].x;
						r.y = pos1[i].y - pos2[j].y;
						r.z = pos1[i].z - pos2[j].z;

						float rinv = (float)(1.0f / Math.sqrt(r.x*r.x + r.y*r.y + r.z*r.z + EPS));
						float rinv3 = rinv * rinv * rinv;

						accel[i].x -= mass1[j]*rinv3*r.x;
						accel[i].y -= mass1[j]*rinv3*r.y;
						accel[i].z -= mass1[j]*rinv3*r.z;

						accel[j].x += mass1[i]*rinv3*r.x;
						accel[j].y += mass1[i]*rinv3*r.y;
						accel[j].z += mass1[i]*rinv3*r.z;
					}
			else
				for (int i = iBeginA; i < iEndA; ++i )
					for (int j = i + 1; j < iEndA; ++j )
					{
						r.x = pos1[i].x - pos2[j].x;
						r.y = pos1[i].y - pos2[j].y;
						r.z = pos1[i].z - pos2[j].z;

						float rinv = (float)(1.0f / Math.sqrt(r.x*r.x + r.y*r.y + r.z*r.z + EPS));
						float rinv3 = rinv * rinv * rinv;

						accel[i].x -= mass2[j]*rinv3*r.x;
						accel[i].y -= mass2[j]*rinv3*r.y;
						accel[i].z -= mass2[j]*rinv3*r.z;
					}		
		}
		return null;
	}

}

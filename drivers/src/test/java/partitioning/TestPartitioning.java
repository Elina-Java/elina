package partitioning;

import static org.junit.Assert.assertTrue;
import hierarchy.HierarchySerializeTest;

import org.junit.Before;
import org.junit.Test;

import partitioners.L1IterativePartitioner;
import service.SOMDTask;
import core.collective.Distribution;
import drivers.HierarchyLevel;

public class TestPartitioning {
	
	class TestDistribution implements Distribution<Integer[]>
	{
		int nparts;
		int[] elems;
		
		public TestDistribution(int nelems)
		{
			elems = new int[nelems];
		}


		public Integer[] [] distribution() {
			int elemsPerPartition=elems.length/nparts;
			Integer[][] partitions = new Integer[nparts][elemsPerPartition];
			return partitions;
		}

		@Override
		public void setPartitions(int length) {
			nparts=length;
		}


		@Override
		public int getElementSize() {
			return 4;
		}

		@Override
		public float getAverageLineSize(int nParts) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Integer[][] distribution(int nParts) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public float getAveragePartitionSize(int nParts) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	@SuppressWarnings("serial")
	class DummyTask extends SOMDTask<Void>
	{

		@Override
		public Void call(Object[] partition) {
			return null;
		}
		
	}
	
	private Distribution<Integer[]>[] dists;
	private Distribution<Integer[]> dist1;
	
	@SuppressWarnings("unchecked")
	@Before
	public void init()
	{
		dist1 = new TestDistribution(1000000);
		dists = (Distribution<Integer[]>[]) new Distribution[1];
		dists[0]=dist1;
	}
	
	@Test
	public void testPartitionSizeFitDedicatedL1()
	{
		L1IterativePartitioner ip = new L1IterativePartitioner();
		HierarchyLevel htest1 = HierarchySerializeTest.getTestHierarchy1();
		
		int nParts = ip.getNparts(dists, htest1, new DummyTask());

		assertTrue((1000000/nParts)*dist1.getElementSize() <= htest1.getLevel(HierarchyLevel.L1).size);	
		
	}
	
	@Test
	public void testPartitionSizeFitSharedL1()
	{
		L1IterativePartitioner ip = new L1IterativePartitioner();
		HierarchyLevel htest2 = HierarchySerializeTest.getTestHierarchy2();
		
		int nParts = ip.getNparts(dists, htest2, new DummyTask());
		
		assertTrue((1000000/nParts)*dist1.getElementSize() <= htest2.getLevel(HierarchyLevel.L1).size/htest2.getLevel(HierarchyLevel.L1).siblings[0].length);
	}

}

package hierarchy;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import drivers.HierarchyLevel;

public class HierarchyLevelTest {
	
	private HierarchyLevel getTestHierarchy1()
	{
		int[][] l1Siblings = {{0},{1},{2},{3}};
		HierarchyLevel l1 = new HierarchyLevel(128,l1Siblings,null);
		int[][] l2Siblings = {{0},{1},{2},{3}};
		HierarchyLevel l2 = new HierarchyLevel(512,l2Siblings,l1);
		int[][] l3Siblings = {{0,1},{2,3}};
		HierarchyLevel l3 = new HierarchyLevel(2048,l3Siblings,l2);
		int[][] ramSiblings = {{0,1,2,3}};
		return new HierarchyLevel(4294967296L,ramSiblings,l3);
	}
	
	private HierarchyLevel getTestHierarchy2()
	{
		int[][] l1Siblings = {{0,1},{2,3}};
		HierarchyLevel l1 = new HierarchyLevel(128,l1Siblings,null);
		int[][] l2Siblings = {{0,1},{2,3}};
		HierarchyLevel l2 = new HierarchyLevel(512,l2Siblings,l1);
		int[][] l3Siblings = {{0,1},{2,3}};
		HierarchyLevel l3 = new HierarchyLevel(2048,l3Siblings,l2);
		int[][] ramSiblings = {{0,1,2,3}};
		return new HierarchyLevel(4294967296L,ramSiblings,l3);
	}
	
	//@Test
	public void H1LowestLevelTest()
	{
		HierarchyLevel h1 = getTestHierarchy1();
		assertEquals(128L, h1.getLevel(HierarchyLevel.L1).getSize());
	}
	
	@Test
	public void H1DedicatedLevelsTest()
	{
		HierarchyLevel h1 = getTestHierarchy1();
		HierarchyLevel ded = h1.getDedicatedLevels();
		
		assertEquals(512L, ded.getSize());
		assertEquals(ded.getChildren(), h1.getLevel(HierarchyLevel.L1));
	}
	
	@Test
	public void H2LowestLevelTest()
	{
		HierarchyLevel h1 = getTestHierarchy2();
		assertEquals(128L, h1.getLevel(HierarchyLevel.L1).getSize());
	}
	
	@Test
	public void H2DedicatedLevelsTest()
	{
		HierarchyLevel h1 = getTestHierarchy1();
		HierarchyLevel ded = h1.getDedicatedLevels();
		
		assertEquals(512L, ded.getSize());
	}
}

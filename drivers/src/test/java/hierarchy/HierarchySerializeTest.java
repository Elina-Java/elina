package hierarchy;

import static org.junit.Assert.assertTrue;
import hierarchy.readers.HierarchyReadImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import drivers.HierarchyLevel;
import drivers.HierarchyReadDriver;
import flexjson.JSONSerializer;

public class HierarchySerializeTest {

	
	public static HierarchyLevel getTestHierarchy1()
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
	
	public static HierarchyLevel getTestHierarchy2()
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

	@Test
	//Serialize
	public void Test1() throws IOException 
	{
		HierarchyLevel ram = getTestHierarchy1();
		boolean success=true;
		JSONSerializer ser = new JSONSerializer().exclude("*.class");
		ser.prettyPrint(true);
		FileWriter fw = null;
		try {
			fw = new FileWriter("hierarchy1.json");
		} catch (IOException e) {
			success=false;
		}
		if(success)
			ser.serialize(ram, fw);
		fw.close();
	}
	
	@Test
	//Deserialize
	public void Test2() throws IOException 
	{
		HierarchyReadDriver hrd = new HierarchyReadImpl();
		HierarchyLevel hl = hrd.getHierarchyRoot("hierarchy1.json");
		assertTrue(hl.size==getTestHierarchy1().size);
		File f = new File("hierarchy1.json");
		f.delete();
	}

}

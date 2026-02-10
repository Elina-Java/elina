package elina.utils;

import hierarchy.readers.HierarchyReadImpl;
import hierarchy.writers.HierarchyLevelWriteImpl;
import java.io.IOException;

import drivers.HierarchyLevel;
import drivers.HierarchyReadDriver;
import drivers.HierarchyWriteDriver;

public class ChangeCacheSize {

	public static void main(String[] args) throws IOException {
		if(args.length<2)
		{
			System.out.println("Usage: (L1 | L2 | L3) + NEW_SIZE");
			System.exit(0);
		}
		String tcl = args[0];
		long newSize = Long.parseLong(args[1]);
		
		HierarchyReadDriver hierReader  = new HierarchyReadImpl();
		HierarchyLevel level = hierReader.getHierarchyRoot();
		if(tcl.equals("L1"))
			(level.getLevel(HierarchyLevel.L1)).size=newSize;
		else if(tcl.equals("L2"))
			(level.getLevel(HierarchyLevel.L2)).size=newSize;
		else if(tcl.equals("L3"))
			(level.getLevel(HierarchyLevel.L3)).size=newSize;
		HierarchyWriteDriver writer = new HierarchyLevelWriteImpl(level,"hierarchy.json");
		if(writer.createHierarchyFile())
			System.out.println("File successfully updated.");
		else
			System.out.println("File could not be updated.");
	}

}

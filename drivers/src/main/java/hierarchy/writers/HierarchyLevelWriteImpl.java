package hierarchy.writers;

import java.io.FileWriter;
import java.io.IOException;

import drivers.HierarchyLevel;
import drivers.HierarchyWriteDriver;
import flexjson.JSONSerializer;

public class HierarchyLevelWriteImpl implements HierarchyWriteDriver {
	
	private HierarchyLevel level;
	private String filename;
	
	public HierarchyLevelWriteImpl(HierarchyLevel level, String filename)
	{
		this.level=level;
		this.filename=filename;
	}

	@Override
	public boolean createHierarchyFile() {
		boolean success=true;
		JSONSerializer ser = new JSONSerializer().exclude("*.class");
		ser.prettyPrint(true);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(filename);
		} catch (IOException e) {
			success=false;
		}
		
		ser.serialize(level,fw);
		
		try {
			fw.close();
		} catch (IOException e) {
			success=false;
		}
		
		return success;
	}

	@Override
	public boolean createHierarchyFile(String filename) {
		// TODO Auto-generated method stub
		return false;
	}

}

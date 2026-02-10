package hierarchy.writers;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import drivers.HierarchyLevel;
import drivers.HierarchyWriteDriver;
import flexjson.JSONSerializer;

public class LsCPUHierarchyWriteImpl implements HierarchyWriteDriver {
	
	private Map<String,String> properties;
	private HierarchyLevel ln;
	
	public LsCPUHierarchyWriteImpl()
	{
		properties = new HashMap<String,String>();
		String cmd = "lscpu" ;
		
		Runtime run = Runtime.getRuntime() ;
		Process pr;
		try {
			pr = run.exec(cmd);
			pr.waitFor();

			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line;
			while((line = buf.readLine()) != null)
			{
				String[] keyval = line.split(":");
				keyval[0]=keyval[0].trim();
				keyval[1]=keyval[1].trim();
				properties.put(keyval[0], keyval[1]);
				System.out.println(line) ;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void createLevelNode()
	{
		//ram
		
		//L3 to L1..
	}
	
	private int parseBytes(String s)
	{
		int mult=1;
		if(s.charAt(s.length()-1)=='K')
		{
			mult = (int) Math.pow(2, 10);
		}
		else if(s.charAt(s.length()-1)=='M')
		{
			mult = (int) Math.pow(2, 20);
		}
		if(mult!=1)
			s=s.substring(0,s.length()-2);
		
		return Integer.parseInt(s)*mult;
	}

	@Override
	public boolean createHierarchyFile() {
		boolean success=true;
		JSONSerializer ser = new JSONSerializer().exclude("class");
		ser.prettyPrint(true);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter("hierarchy.json");
		} catch (IOException e) {
			success=false;
		}
		
		ser.serialize(ln,fw);
		
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

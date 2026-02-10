package elina.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ChangeTCL {

	public static void main(String[] args) throws IOException {
		if(args.length<1)
		{
			System.out.println("Usage: L1 | L2 | L3");
			System.exit(0);
		}
		String tcl = args[0];
		
		File dir = new File(".");
		File[] files = dir.listFiles();
		for(File f: files)
		{
			if(f.getName().contains("Config"))
			{
				Scanner sc = new Scanner(f).useDelimiter("\\Z");
				String content = sc.next();
				sc.close();
				int index = content.indexOf("IterativePartitioner");
				String newContent = content.substring(0,content.indexOf("partitioners.")+13)+tcl+content.substring(index);
				FileWriter fw = new FileWriter(f);
				fw.write(newContent);
				fw.close();
			}
		}
	}

}

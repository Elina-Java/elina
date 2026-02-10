package hierarchy.readers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import drivers.HierarchyLevel;
import drivers.HierarchyReadDriver;
import flexjson.JSONDeserializer;

public class HierarchyReadImpl implements HierarchyReadDriver {
	private boolean inMemory=false;
	private HierarchyLevel tree;

	@Override
	public HierarchyLevel getHierarchyRoot()
	{
		if(!inMemory)
		{
			FileReader fr=null;
			try {
				fr = new FileReader("hierarchy.json");
			} catch (FileNotFoundException e) {
				//TODO
				//criar ficheiro de acordo com algum driver?
			}

			JSONDeserializer<HierarchyLevel> deser = new JSONDeserializer<HierarchyLevel>();
			tree = deser.deserialize(fr,HierarchyLevel.class);

			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inMemory=true;
		}
		return tree;
	}

	@Override
	public HierarchyLevel getHierarchyRoot(String filename) {
		if(!inMemory)
		{
			FileReader fr=null;
			try {
				fr = new FileReader(filename);
			} catch (FileNotFoundException e) {
				//TODO
				//criar ficheiro de acordo com algum driver?
			}

			JSONDeserializer<HierarchyLevel> deser = new JSONDeserializer<HierarchyLevel>();
			tree = deser.deserialize(fr,HierarchyLevel.class);

			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inMemory=true;
		}
		return tree;
	}

}

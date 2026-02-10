package communication.nio;

import java.io.File;


public class LocalCommTest extends communication.LocalCommTest {

	@Override
	protected String getElinaConfigFile() {
		return "communication" + File.separator + "nio" + File.separator + "Config.xml";
	}

	
}

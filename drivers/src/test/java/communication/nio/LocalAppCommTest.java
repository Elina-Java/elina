package communication.nio;

import java.io.File;


public class LocalAppCommTest extends communication.LocalAppCommTest {

	@Override
	protected String getElinaConfigFile() {
		return "communication" + File.separator + "nio" + File.separator + "Config.xml";
	}

	
}

package elina.tests;

import java.io.File;



import elina.ApplicationLauncher;
import org.junit.jupiter.api.Test;

public abstract class BaseTest {

	private static final String baseResouceFolder = System.getProperty("user.dir") + File.separator + "src"
			+ File.separator + "test" + File.separator + "resources" + File.separator;
	
	protected abstract String getElinaConfigFile();
	
	protected abstract void test(); 
	
	@Test
	public void runTest() {
		ApplicationLauncher.init(baseResouceFolder + getElinaConfigFile());
		test();
	}

}

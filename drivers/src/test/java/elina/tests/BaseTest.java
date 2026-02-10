package elina.tests;

import java.io.File;

import org.junit.Test;

import elina.ApplicationLauncher;

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

package instrumentation;


import org.junit.jupiter.api.Test;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public abstract class ProcessorTest {
	
	private static final String Elina_Processor = "instrumentation.Processor";
	private static final String Base_Dir = System.getProperty("user.dir") + File.separator + "target";

	private static final String Class_Output_Folder = Base_Dir + File.separator + "test-classes";
	private static final String Source_Output_Folder = Base_Dir + File.separator + "generated-sources";
	
	
	protected abstract String getServiceName() ;
	protected abstract String getProviderName() ;
	
	@Test
	public void test() {
		
		File dir = new File(Source_Output_Folder);
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(Class_Output_Folder);
		if (!dir.exists())
			dir.mkdirs();
		
		
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		javaCompiler.run(null, null, null, "-Xlint:unchecked",
						"-processor", Elina_Processor, 
						"-d", Class_Output_Folder,  
						"-s", Source_Output_Folder, 
						getServiceName(),
						getProviderName()
					);

	}
}

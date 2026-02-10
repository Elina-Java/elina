package logging;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4J implements drivers.Logger {

	static {
		PropertyConfigurator.configure(Log4J.class.getClassLoader().getResource("log4j.properties"));
	}
	
	@Override
	public void info(Class<?> c, Object message) {
		Logger.getLogger(c).info(message);
	}

	@Override
	public void debug(Class<?> c, Object message) {
		Logger.getLogger(c).debug(message);
	}

	@Override
	public void error(Class<?> c, Object message) {
		Logger.getLogger(c).error(message);
	}

	@Override
	public void warning(Class<?> c, Object message) {
		Logger.getLogger(c).warn(message);
	}

	@Override
	public void error(Class<?> c, Object message, Exception e) {
		Logger.getLogger(c).error(message, e);
		
	}

	@Override
	public void fatal(Class<?> c, Object message) {
		Logger.getLogger(c).fatal(message);
	}
	
	@Override
	public void fatal(Class<?> c, Exception e) {
		Logger.getLogger(c).fatal(e);
		
	}
}

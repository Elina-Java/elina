package logging;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.net.URISyntaxException;
import java.util.Objects;

public class Log4J implements drivers.Logger {

	static {
        LoggerContext context  = (LoggerContext)LogManager.getContext(false);
        try {
            context.setConfigLocation(Objects.requireNonNull(Log4J.class.getClassLoader().getResource("log4j.properties")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	public void info(Class<?> c, Object message) {
		LogManager.getLogger(c).info(message);
	}

	@Override
	public void debug(Class<?> c, Object message) {
		LogManager.getLogger(c).debug(message);
	}

	@Override
	public void error(Class<?> c, Object message) {
		LogManager.getLogger(c).error(message);
	}

	@Override
	public void warning(Class<?> c, Object message) {
		LogManager.getLogger(c).warn(message);
	}

	@Override
	public void error(Class<?> c, Object message, Exception e) {
		LogManager.getLogger(c).error(message, e);
		
	}

	@Override
	public void fatal(Class<?> c, Object message) {
		LogManager.getLogger(c).fatal(message);
	}
	
	@Override
	public void fatal(Class<?> c, Exception e) {
		LogManager.getLogger(c).fatal(e);
		
	}
}

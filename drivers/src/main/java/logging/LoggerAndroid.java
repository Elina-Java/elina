package logging;

import android.util.Log;

public class LoggerAndroid implements drivers.Logger {

	@Override
	public void info(Class<?> c, Object message) {
		Log.i(c.getName(), message.toString());

	}

	@Override
	public void debug(Class<?> c, Object message) {
		Log.d(c.getName(), message.toString());

	}

	@Override
	public void error(Class<?> c, Object message) {
		Log.e(c.getName(), message.toString());

	}

	@Override
	public void warning(Class<?> c, Object message) {
		Log.w(c.getName(), message.toString());

	}

	@Override
	public void fatal(Class<?> c, Object message) {
		Log.e(c.getName(), message.toString());

	}

	@Override
	public void error(Class<?> c, Object message, Exception e) {
		Log.e(c.getName(), message.toString(), e);

	}

	@Override
	public void fatal(Class<?> c, Exception e) {
		Log.e(c.getName(), e.getMessage(), e);

	}

}

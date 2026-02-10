package drivers;


/**
 * 
 * @author Filipa Ferreira n32032
 *
 */
public interface Logger {
	
	
	/**
	 * Método info do Logger
	 * @param message - mensagem passada por parametro
	 */
	public void info(Class<?> c, Object message);
	
	/**
	 * Metodo debug do Logger
	 * @param message - mensagem passada por parametro
	 */
	public void debug(Class<?> c, Object message);
	
	/**
	 * Metodo error do Logger
	 * @param message - mensagem passada por parametro
	 */
	public void error(Class<?> c, Object message);
	
	/**
	 * Metodo warn do Logger
	 * @param message - mensagem passada por parametro
	 */
	public void warning(Class<?> c, Object message);
	
	/**
	 * Metodo fatal do Logger
	 * @param message - mensagem passada por parametro
	 */
	public void fatal(Class<?> c, Object message);

	void error(Class<?> c, Object message, Exception e);

	void fatal(Class<?> c, Exception e);

}

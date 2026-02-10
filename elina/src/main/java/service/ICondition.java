package service;

/**
 * 
 * @author João Saramago
 * @since Dec 9, 2011 5:39:51 PM
 */
public interface ICondition {

	public void condWait();
	public void condNotify();
	public void condNotifyAll();
}

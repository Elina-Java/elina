package synchronization.locks;

import java.util.concurrent.locks.Lock;

import service.ConditionCode;
import service.ICondition;



/**
 * 
 * Classe que ...
 * 
 * @author Diogo Mourão
 *
 */

public final class Condition implements ICondition{
	private java.util.concurrent.locks.Condition cond;
	private ConditionCode conditionTest;
	private Lock lock;

	/**
	 * 
	 * Construtor.
	 * 
	 * @param lock - Lock que está associado a condição.
	 * @param conditionTest
	 */
	public Condition(Lock lock, ConditionCode conditionTest)
	{
		this.cond = lock.newCondition();
		this.conditionTest = conditionTest;
		this.lock=lock;
	}

	/**
	 * Método que adormece o thread corrente até que seja acordado e a condição seja verdadeira.
	 */
	public void condWait()
	{
		try {
			
			/*Aqui podia estar o teste inverso.
			 * O utilizador terá que saber que se a sua condição for falsa, o thread adormece.*/
			lock.lock();
			while(!conditionTest.call()) 
			{
				
				this.cond.await();
				
			}
			lock.unlock();

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Método que ...
	 */
	public void condNotify()
	{
		this.lock.lock();
		this.cond.signal();
		this.lock.unlock();
	}

	/**
	 * Método que ...
	 */
	public void condNotifyAll()
	{
		this.lock.lock();
		this.cond.signalAll();
		this.lock.unlock();
	}
	
}

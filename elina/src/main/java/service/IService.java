
package service;


import java.io.Serializable;
import java.util.UUID;

import javax.jws.WebMethod;

import core.Level;




/**
 *
 *  Um serviço representa uma entidade de computação. Pode representar uma máquina, um CPU, um GPU ou alguma entidade lógica de computação.
 *	Cada localidade é representada por um conjunto de dados (objectos) e por um conjunto de actividades (tarefas) que operam sobre esses dados e que
 * retornam, ou não, um ou mais resultados. As actividades (vistas como threads) que computam as tarefas são executadas concorrentemente.
 *  
 *  Desta forma, cada localidade fornece uma API para que seja possível executar tarefas nessa localidade.
 *  
 *  Importante de realçar de que o conjunto de localidades não é homegeneo, podendo uma localidade estar mapeada num CPU e outra estar mapeada num GPU.
 *
 * @author Diogo Mourão
 *
 */

public interface IService extends Serializable{
	
	
	/**
	 * 
	 * Método que ...
	 * 
	 * @param <R>
	 * @param methodName
	 * @param args
	 * @return Futuro que representa a computação assincrona e permite obter o seu resultado, quando disponível.
	 * @throws NoSuchMethodException 
	 */
	@WebMethod(exclude = true)
	<R> IFuture<R> invoke(String methodName, Object[] args)  throws NoSuchMethodException;
	
	@WebMethod(exclude = true)
	<R> IFuture<R> invoke(String methodName, Object[] args,Class<?>[] types)  throws NoSuchMethodException;


	
	
	
	
	/**
	 * 
	 * @param <T>
	 * @param <P>
	 * @param <R>
	 * @param distr
	 * @param red
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException 
	 */
	//<T,R> IFuture<R> distReduce(Distribution<?>[] distr, Reduction<R> red, String methodName, int index ,Object ...args) throws NoSuchMethodException;

	

	@WebMethod(exclude = true)
	UUID getClientId();

	@WebMethod(exclude = true)
	UUID getID();
	
	@WebMethod(exclude = true)
	public void cancel();

	@WebMethod(exclude = true)
	public void setAffinity(IService p);
	
	@WebMethod(exclude = true)
	public void setImpAffinity(IService p);
	
	@WebMethod(exclude = true)
	Level getLevel();
	
	
	
}
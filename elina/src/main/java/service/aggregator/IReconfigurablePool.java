package service.aggregator;

import javax.jws.WebMethod;

import service.IReconfigurable;

public interface IReconfigurablePool extends IReconfigurable {

	
	@WebMethod(exclude = true)
	<R> void add(R provider);

	@WebMethod(exclude = true)
	<R>  void remove(R provider);
	
	@WebMethod(exclude = true)
	<R>  void removeAll(R provider);
}

package service;

import java.net.URL;

import javax.jws.WebMethod;

public interface IReconfigurable {
	
	@WebMethod(exclude = true)
	void replaceFromSystem();
	
	@WebMethod(exclude = true)
	void replace(URL url);
	
	@WebMethod(exclude = true)
	<S> void replace(S newProvider);
	
	@WebMethod(exclude = true)
	void pause();
	
	@WebMethod(exclude = true)
	void resume();


}

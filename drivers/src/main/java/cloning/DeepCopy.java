package cloning;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.rits.cloning.Cloner;

import drivers.CloningDriver;
import elina.Elina;

public class DeepCopy 
implements CloningDriver{

	private Cloner cloner;
	private static Logger logger = LogManager.getLogger(DeepCopy.class);
	
	public DeepCopy()
	{
		this.cloner = new Cloner();	
	}
	
	@Override
	public <T> T copy(T object) {
		if(Elina.DEBUG)
			logger.debug("Clone Object "+object);
		return this.cloner.deepClone(object);
	}

}

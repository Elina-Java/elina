package cloning;

import com.rits.cloning.Cloner;

import drivers.CloningDriver;

public class ShallowCopy 
implements CloningDriver{

	private Cloner cloner;
	
	public ShallowCopy()
	{
		this.cloner = new Cloner();
	}
	
	@Override
	public <T> T copy(T object) {
		return this.cloner.shallowClone(object);
	}

}

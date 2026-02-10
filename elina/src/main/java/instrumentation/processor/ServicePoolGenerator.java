package instrumentation.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;

import instrumentation.Constants;



public class ServicePoolGenerator extends ClassGenerator {

	public ServicePoolGenerator(Messager messager) {
		super(Constants.SERVICE_POOL, "Pool", messager, true);
	}

	@Override
	protected String getConstructor(String name) {
		return "public " + name + "( " +
				Constants.SERVICE_INTERFACE + "[] workers, " +
				Constants.SERVICE_SCHEDULER + " scheduler) " +
				"{\n\t\tsuper((" + this.className + "[]) workers, scheduler);\n\t}";
	}
	
	protected List<String> getSuperClassTypeParameters() {
		List<String> typeParameters = new ArrayList<String>();
		typeParameters.add(this.className);
		return typeParameters;
	}

/*	
	@Override
	protected List<String> getSuperClassTypeParameters() {
		List<String> typeParameters = new ArrayList<String>();
		
		try {
			Class<?> cls = Class.forName(this.packageName + "." + this.className);
			typeParameters.add(getService(cls).getCanonicalName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return typeParameters;
	}

	
	private static Class<?> getService(Class<?> c) {
		for (Class<?> interf : c.getInterfaces()) {
					
			if (interf.equals(IService.class)) 
				return c;	
			
			if (IService.class.isAssignableFrom(interf))
				return getService(interf);
		}
		return null;
	}*/
}

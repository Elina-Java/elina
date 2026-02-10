package service.aggregator;


import service.Service;

public class Facade<T1, T2> extends Service {
	
	
	private static final long serialVersionUID = 1L;
	private T1 servive1;
	private T2 service2;
	
	public Facade(T1 service1, T2 service2) {
		this.servive1 = service1;
		this.service2 = service2;
	}
	
	public T1 getService1() {
		return servive1;
	}
	
	public T2 getService2() {
		return service2;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T get(Class<T> Tclass) throws FacadeTypeNotFound {
		if (servive1.getClass().equals(Tclass)||contains(servive1.getClass().getInterfaces(), Tclass))
				return (T)servive1;
		else {
			if (service2.getClass().equals(Tclass)||contains(service2.getClass().getInterfaces(), Tclass))
					return (T)service2;
			else
				if (servive1 instanceof Facade)
					try {
						return (T)((Facade)servive1).get(Tclass);
					}
					catch (FacadeTypeNotFound e) {
						if (service2 instanceof Facade)
							return (T)((Facade)service2).get(Tclass);
					}
				else {
					if (service2 instanceof Facade)
						return (T)((Facade)service2).get(Tclass);
				}
		}	
		throw new FacadeTypeNotFound();
	}
	
	private boolean contains(Class<?>[] interfaces,Class<?> i){
		for (Class<?> class1 : interfaces) {
			if(class1.equals(i))
				return true;
		}
		return false;
	}

}

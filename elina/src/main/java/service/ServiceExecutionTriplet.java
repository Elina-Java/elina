package service;

class ServiceExecutionTriplet<T, U, V> {
	T a;
	U b;
	V c;

	ServiceExecutionTriplet(T a, U b, V c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	T getA() {
		return a;
	}

	U getB() {
		return b;
	}

	V getC() {
		return c;
	}
}

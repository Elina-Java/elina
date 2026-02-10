package javaGrande.elina.multicore.montecarlo;

public class DemoException extends java.lang.Exception {

	private static final long serialVersionUID = 1L;

	public static boolean DEBUG = true;

	public DemoException() {
		super();
		if (DEBUG) {
			printStackTrace();
		}
	}

	public DemoException(String s) {
		super(s);
		if (DEBUG) {
			printStackTrace();
		}
	}

	public DemoException(int ierr) {
		super(String.valueOf(ierr));
		if (DEBUG) {
			printStackTrace();
		}
	}
}

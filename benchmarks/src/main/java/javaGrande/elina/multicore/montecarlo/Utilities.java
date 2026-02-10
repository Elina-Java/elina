package javaGrande.elina.multicore.montecarlo;

public final class Utilities {

	public static String[] splitString(String splitChar, String arg) {

		String myArgs[];
		int nArgs = 0;
		int foundIndex = 0, fromIndex = 0;

		while ((foundIndex = arg.indexOf(splitChar, fromIndex)) > -1) {
			nArgs++;
			fromIndex = foundIndex + 1;
		}

		myArgs = new String[nArgs + 1];
		nArgs = 0;
		fromIndex = 0;
		while ((foundIndex = arg.indexOf(splitChar, fromIndex)) > -1) {
			myArgs[nArgs] = arg.substring(fromIndex, foundIndex);
			nArgs++;
			fromIndex = foundIndex + 1;
		}
		myArgs[nArgs] = arg.substring(fromIndex);
		return myArgs;
	}
}

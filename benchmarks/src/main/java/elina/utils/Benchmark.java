package elina.utils;

public class Benchmark {

	protected static int NEXECS = 1;
	protected static int PROBSIZE = 0;
	protected static int NSITES = 1;

	public static void parse(String[] args, String className) {
		if (args.length > 3) {
			System.out.println("Usage:");
			System.out.println("java " + className + " n_execs problem_size number_sites");
			System.exit(0);
		}
		switch (args.length) {
		case 3:
			NSITES = Integer.parseInt(args[2]);
		case 2:
			PROBSIZE = Integer.parseInt(args[1]);
		case 1:
			NEXECS = Integer.parseInt(args[0]);
		}
	}
}

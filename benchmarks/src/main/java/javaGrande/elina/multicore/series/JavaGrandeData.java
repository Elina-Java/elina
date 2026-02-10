package javaGrande.elina.multicore.series;


public final class JavaGrandeData {
	
	public static final int[] sizes = new int[] { 
									10000, 100000, 1000000  // reference JavaGrande configurations (A, B, C)
								};
	public static int NUMBER_OF_PROBLEMS = sizes.length;
	
	
	/*
	 * TrapezoidIntegrate
	 *
	 * Perform a simple trapezoid integration on the function (x+1)**x.
	 * x0,x1 set the lower and upper bounds of the integration.
	 * nsteps indicates # of trapezoidal sections.
	 * omegan is the fundamental frequency times the series member #.
	 * select = 0 for the A[0] term, 1 for cosine terms, and 2 for
	 * sine terms. Returns the value.
	 */

	public static double TrapezoidIntegrate (double x0,     // Lower bound.
			double x1,                // Upper bound.
			int nsteps,               // # of steps.
			double omegan,            // omega * n.
			int select)               // Term type.
	{
		double x;               // Independent variable.
		double dx;              // Step size.
		double rvalue;          // Return value.

		// Initialize independent variable.

		x = x0;

		// Calculate stepsize.

		dx = (x1 - x0) / (double)nsteps;

		// Initialize the return value.

		rvalue = thefunction(x0, omegan, select) / (double)2.0;

		// Compute the other terms of the integral.

		if (nsteps != 1)
		{
			--nsteps;               // Already done 1 step.
			while (--nsteps > 0)
			{
				x += dx;
				rvalue += thefunction(x, omegan, select);
			}
		}

		// Finish computation.

		rvalue=(rvalue + thefunction(x1,omegan,select) / (double)2.0) * dx;
		return(rvalue);
	}

	/*
	 * thefunction
	 *
	 * This routine selects the function to be used in the Trapezoid
	 * integration. x is the independent variable, omegan is omega * n,
	 * and select chooses which of the sine/cosine functions
	 * are used. Note the special case for select=0.
	 */

	 private static double thefunction(double x,      // Independent variable.
			double omegan,              // Omega * term.
			int select)                 // Choose type.
	{

		// Use select to pick which function we call.

		switch(select)
		{
		case 0: return(Math.pow(x+(double)1.0,x));

		case 1: return(Math.pow(x+(double)1.0,x) * Math.cos(omegan*x));

		case 2: return(Math.pow(x+(double)1.0,x) * Math.sin(omegan*x));
		}

		// We should never reach this point, but the following
		// keeps compilers from issuing a warning message.

		return (0.0);
	}
	
	public static void validate(double[][] result) {
		double ref[][] = { { 2.8729524964837996, 0.0 },
				{ 1.1161046676147888, -1.8819691893398025 },
				{ 0.34429060398168704, -1.1645642623320958 },
				{ 0.15238898702519288, -0.8143461113044298 } };
		/*
		 * // for 200 points double ref[][] = {{2.8377707562588803, 0.0},
		 * {1.0457844730995536, -1.8791032618587762}, {0.27410022422635033,
		 * -1.158835123403027}, {0.08241482176581083, -0.8057591902785817}};
		 */

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				double error = Math.abs(result[j][i] - ref[i][j]);
				if (error > 1.0e-12) {
					System.out.println("Validation failed for coefficient " + j
							+ "," + i);
					System.out.println("Computed value = " + result[j][i]);
					System.out.println("Reference value = " + ref[i][j]);
				}
				// System.out.println("Computed value = " + result[j][i]);
			}
		}
	}
}

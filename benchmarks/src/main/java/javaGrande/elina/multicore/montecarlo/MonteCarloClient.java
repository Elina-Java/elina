package javaGrande.elina.multicore.montecarlo;

import java.io.IOException;
import java.util.List;

import elina.utils.Evaluation;



public class MonteCarloClient {

	private int EXECS;
	private MonteCarloService service;
	


	public MonteCarloClient(MonteCarloService service, int nexecs) {
		this.EXECS = nexecs;
		this.service = service;
	}




	public void run() throws DemoException, IOException {
		RatePath rateP = new RatePath("data/hitData");
		ReturnPath returnP = rateP.getReturnCompounded();
		returnP.estimatePath();
		// double expectedReturnRate = returnP.get_expectedReturnRate();
		// double volatility = returnP.get_volatility();
		returnP.get_expectedReturnRate();
		returnP.get_volatility();

		double pathStartValue = 100.0;

		for (int i = 0; i < JavaGrandeData.NUMBER_OF_PROBLEMS; i++) {	
			Evaluation eval = new Evaluation(EXECS);
			int size = JavaGrandeData.sizes[i];
			
			for (int j = 0; j < EXECS; j++) {
				eval.writeAppName("montecarlo", size);
				eval.startTimer();

				ToInitAllTasks initAllTasks = new ToInitAllTasks(returnP, JavaGrandeData.nTimeStepsMC, pathStartValue);
				List<ToResult> results = service.runThread(new int[] { 0, size }, initAllTasks);

				eval.stopTimer();
				processSerial(results, size);
				eval.writeTime();
				// validate(v);

			}

			eval.writeAverage();
		}

	}

	private double processSerial(List<ToResult> results, int size) throws DemoException {
		double avgExpectedReturnRateMC = 0.0;
		@SuppressWarnings("unused")
		double avgVolatilityMC = 0.0;

		if (size != results.size()) {
			// System.err
			// .println("Fatal: TaskRunner managed to finish with no all the results gathered in!");

		}
		//
		// Create an instance of a RatePath, for accumulating the results of the
		// Monte Carlo simulations.
		RatePath avgMCrate = new RatePath(JavaGrandeData.nTimeStepsMC, "MC", 19990109,	19991231, JavaGrandeData.dTime);
		for (ToResult returnMC : results) {

			// int i = 0; i < size; i++) {
			// First, create an instance which is supposed to generate a
			// particularly simple MC path.
			// returnMC = results.get(i);
			avgMCrate.inc_pathValue(returnMC.get_pathValue());
			avgExpectedReturnRateMC += returnMC.get_expectedReturnRate();
			avgVolatilityMC += returnMC.get_volatility();
			// runAvgExpectedReturnRateMC = avgExpectedReturnRateMC / ((double)
			// (i + 1));
			// runAvgVolatilityMC = avgVolatilityMC / ((double) (i + 1));
		} // for i;
		avgMCrate.inc_pathValue((double) 1.0 / ((double) size));
		avgExpectedReturnRateMC /= size;
		avgVolatilityMC /= size;
		/*
		 * try{ Thread.sleep(200); } catch( InterruptedException intEx) {
		 * errPrintln(intEx.toString()); }
		 */

		return avgExpectedReturnRateMC;

		// dbgPrintln("Average over "+nRunsMC+": expectedReturnRate="+
		// avgExpectedReturnRateMC+" volatility="+avgVolatilityMC +
		// JGFavgExpectedReturnRateMC);
	}

	public void validate(double v, int size) {
		double refval[] = { -0.0333976656762814, -0.03215796752868655 };

		double dev = Math.abs(v - refval[1]);
		if (dev > 1.0e-12) {
			System.out.println("Validation failed");
			System.out.println(" expectedReturnRate= " + v + "  " + dev + "  "
					+ size);
		}
	}
}

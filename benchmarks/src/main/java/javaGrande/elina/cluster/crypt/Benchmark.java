package javaGrande.elina.cluster.crypt;

import java.net.MalformedURLException;

import javaGrande.elina.multicore.crypt.JavaGrandeData;
import javaGrande.elina.multicore.crypt.withReturnValue.CryptService;

import service.Application;
import service.RemoteException;
import service.aggregator.ServiceAggregator;
import elina.ApplicationLauncher;
import elina.utils.PartitionedTimer;

public class Benchmark extends elina.utils.Benchmark {

	public static void main(String[] args) throws MalformedURLException, RemoteException {
		
		parse(args, Benchmark.class.getName());
		if (PROBSIZE > 0) {
			JavaGrandeData.sizes[0] = PROBSIZE;
			JavaGrandeData.NUMBER_OF_PROBLEMS = 1;
			PartitionedTimer.NEXECS=NEXECS;
		}

		ApplicationLauncher.init(); // "127.0.0.1");
		Application app = Application.newInstance();
		
		/* Service */
		CryptService crypt = ServiceAggregator.distRed(CryptServer.class, NSITES);
		System.out.println("CLLLL " + crypt.getClass());
		app.addService(crypt);

		/* Client */
		app.addService(new CryptClient(crypt, NEXECS, NSITES));

		/* Deploy application */
		ApplicationLauncher.deploy(app);
		ApplicationLauncher.stop(app);

	}

}

package javaGrande.elina.cluster.series;


import java.net.MalformedURLException;

import javaGrande.elina.multicore.series.JavaGrandeData;

import service.ActiveService;
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
			PartitionedTimer.NEXECS = NEXECS;
		}
			
		
		Application app = Application.newInstance(); 
		ApplicationLauncher.init();

		SeriesService seriesService = ServiceAggregator.distRed(SeriesProvider.class, NSITES);
		ActiveService client = new SeriesClient(seriesService,NEXECS,NSITES);

		
		app.addService(seriesService);
		app.addService(client);
		
		ApplicationLauncher.deploy(app);
	
	}

}

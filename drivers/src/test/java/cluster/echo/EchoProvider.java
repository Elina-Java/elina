package cluster.echo;


import java.net.MalformedURLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import org.junit.jupiter.api.Test;
import service.ActiveService;
import service.Application;
import service.RemoteException;
import elina.ApplicationLauncher;

@Suite
@SelectClasses({ EchoProvider.class, Client.class })
public class EchoProvider extends ActiveService implements EchoService {

	@Test
	public void run() {
		try {
			register();
			System.out.println("Registered the service");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String echo(String s) {
		return s;
	}

	@BeforeAll
	public static void echoServer() throws MalformedURLException, RemoteException {
		ApplicationLauncher.init("127.0.0.1");
		Application app = Application.newInstance(); 
		app.addService(new EchoProvider());
		app.addToClassPath("Applications-0.0.1-SNAPSHOT.jar");
		ApplicationLauncher.deploy(app);	
	}
}

package cluster.echo;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;

import service.Application;
import service.RemoteException;
import service.Service;
import elina.ApplicationLauncher;

@SuiteClasses( { EchoProvider.class, Client.class })
public class Client extends Application {

	@BeforeClass
	public  static void echoClient() throws MalformedURLException, RemoteException {
		ApplicationLauncher.init("127.0.0.1");
		Client c = new Client();
		c.addToClassPath("Applications-0.0.1-SNAPSHOT.jar");
		ApplicationLauncher.deploy(c);
	}

	@Test
	public void run() {
		System.out.println("oo");
		try {
			EchoService echo = Service.<EchoService>lookup("cluster.echo.EchoProvider");
			assertEquals("Hello world!", echo.echo("Hello world!"));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}

package cluster.echo;

import java.net.MalformedURLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import service.Application;
import service.RemoteException;
import service.Service;
import elina.ApplicationLauncher;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Suite
@SelectClasses( { EchoProvider.class, Client.class })
public class Client extends Application {

	@BeforeAll
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

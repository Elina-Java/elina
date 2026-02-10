package communication;


import service.Application;
import service.RemoteException;
import elina.ApplicationLauncher;
import elina.tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class LocalAppCommTest extends BaseTest {

	protected void test() {
		Application app = Application.newInstance();
		EchoProvider echo = new EchoProvider();
		EchoService echoStub = (EchoService) echo.createStub();
		app.addService(echo);
		app.addService(echoStub);
		try {
			ApplicationLauncher.deploy(app);
			assertEquals("Hello world!", echoStub.echo("Hello world!"));
		} catch (RemoteException e) {
			assert(false);
			e.printStackTrace();
		}
	}
}

package communication;

import static org.junit.Assert.assertEquals;
import service.Application;
import service.RemoteException;
import elina.ApplicationLauncher;
import elina.tests.BaseTest;

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

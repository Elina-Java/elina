package communication;

import static org.junit.Assert.assertEquals;
import elina.tests.BaseTest;

public abstract class LocalCommTest extends BaseTest {

	protected void test() {
		EchoService echo = (EchoService) new EchoProvider().createStub();
		assertEquals("Hello world!", echo.echo("Hello world!"));
	}
}

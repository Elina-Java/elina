package communication;

import elina.tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class LocalCommTest extends BaseTest {

	protected void test() {
		EchoService echo = (EchoService) new EchoProvider().createStub();
		assertEquals("Hello world!", echo.echo("Hello world!"));
	}
}

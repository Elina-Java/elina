package core.init;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

	
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}

	
	public void warning(SAXParseException e) throws SAXException {
		System.err.println("Warning: " + e.getLocalizedMessage());
	}

}

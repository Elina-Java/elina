package core.init;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.Level;
import core.communication.CommunicationModule;
import elina.Elina;

/**
 * Class holding the parameterization of the middleware retrieved from the supplied configuration file
 * 
 * @author João Saramago
 * 
 * Revised by Hervé Paulino
 */
public class Configuration {

	/**
	 * Default name for the configuration file
	 */
	public static final String DefaultConfigurationFile = "Config.xml";

	/**
	 * Identifier of Elina's environment variable for setting up the configuration file's location
	 */
	private static final String ElinaEnvironmentVariable = "ELINA_CONFIG";


	private String communication;
	private Map<String, String> commParams;
	private String synchronization;
	private String copy;
	private String consistencyModel;
	private Map<Level, String> distrReduce = new HashMap<Level, String>();
	private String scheduling;
	private String schRank;

	// Nuno Delgado
	private String taskExecutor;
	private String hierarchyReader;
	private String partitioner;
	private String affinity;
	private String aggregator;
	private int nworkers;


	private String logger;	

	private String barrier;

	public Configuration(String file) throws ConfigurationException, IOException {

		try {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setResourceResolver(new ResourceResolver());
			StreamSource configStream = new StreamSource(getClass()
					.getClassLoader().getResourceAsStream("config.xsd"), "config.xsd");
			Schema schema = factory.newSchema(configStream);

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setSchema(schema);
			builderFactory.setValidating(false);
			builderFactory.setNamespaceAware(true);

			Map<String, String> env = System.getenv();
			if (env.containsKey(ElinaEnvironmentVariable))
				file = env.get(ElinaEnvironmentVariable);

			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			builder.setErrorHandler(new XMLErrorHandler());
			Document document = builder.parse(new FileInputStream(file));

			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(document));

			Element config = document.getDocumentElement();
			searchDom(config);

		} catch (ParserConfigurationException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			throw new ConfigurationException(e.getMessage());
		}
	}

	private void searchDom(Element elm) {
		NodeList nodes = elm.getChildNodes();

		if (elm.getNodeName().equals("config")) {
			Elina.DEBUG = Boolean.parseBoolean(elm.getAttribute("debug"));
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node instanceof Element) {
				// a child element to process
				Element child = (Element) node;

				if (child.getNodeName() == "communication") {
					this.communication = child.getTextContent();
					NamedNodeMap atributes = child.getAttributes();
					this.commParams = new HashMap<String, String>(
							atributes.getLength());
					for (int j = 0; j < atributes.getLength(); j++) {
						Attr a = ((Attr) atributes.item(j));
						this.commParams.put(a.getName(), a.getValue());
					}
				}

				if (child.getNodeName() == "synchronization")
					this.synchronization = child.getTextContent();

				if (child.getNodeName() == "copy")
					this.copy = child.getTextContent();

				if (child.getNodeName() == "barrier")
					this.barrier = child.getTextContent();

				if (child.getNodeName() == "consistencyModel")
					this.consistencyModel = child.getTextContent();

				if (child.getNodeName() == "distrReduce")
					this.distrReduce.put(Level.Node, child.getTextContent());

				if (child.getNodeName() == "domainDecomposition_level") {
					this.distrReduce.put(
							Level.valueOf(child.getAttribute("level")),
							child.getTextContent());
				}

				if (child.getNodeName() == "node") {
					CommunicationModule.addNode(child.getTextContent(), Integer.parseInt(child
							.getAttribute("port")));
				}

				if (child.getNodeName() == "scheduling") {
					this.scheduling = child.getTextContent();
					this.schRank = child.getAttribute("rank");
				}

				if (child.getNodeName() == "taskExecutor") {
					this.taskExecutor = child.getTextContent();
					this.nworkers = Integer.parseInt(child
							.getAttribute("nWorkers"));
				}
				
				if (child.getNodeName() == "aggregator")
					this.aggregator = child.getTextContent();
				
				if (child.getNodeName() == "affinityMapper")
					this.affinity = child.getTextContent();
				
				if (child.getNodeName() == "hierarchyReader")
					this.hierarchyReader = child.getTextContent();

				if (child.getNodeName() == "partitioner")
					this.partitioner = child.getTextContent();

				if (child.getNodeName() == "logging")
					this.logger = child.getTextContent();
				
				this.searchDom(child);
			}
		}
	}



	public String getCommunication() {
		return communication;
	}

	public String getSynchronization() {
		return synchronization;
	}

	public String getCopy() {
		return copy;
	}

	public String getConsistencyModel() {
		return consistencyModel;
	}

	public String getDomainDecomposition(Level level) {
		return distrReduce.get(level);
	}

	public Map<String, String> getComParams() {
		return commParams;
	}

	public String getScheduling() {
		return scheduling;
	}

	public String getTaskExecutor() {
		return taskExecutor;
	}

	public int getNworkers() {
		return nworkers;
	}

	public String getSchRank() {
		return schRank;
	}

	public String getBarrier() {
		return barrier;
	}

	//TODO
	public String getHierarchyReader()
	{
		return hierarchyReader;
	}

	//TODO
	public String getPartitioner()
	{
		return partitioner;
	}
	
	//TODO
	public String getAffinityMapper()
	{
		return affinity;
	}
	
	public String getAggregator()
	{
		return aggregator;
	}
	
	public String getLogger()
	{
		System.err.println(logger);
		return logger;
	}
	
	
	/*@SuppressWarnings("rawtypes")
	public core.communication.Node getLocalNode() {

			for (core.communication.Node n : nodes) {
				System.out.println(n.getAddr() + ":" + n.getPort()  + "--" + n.isLocal() );
			if (n.isLocal())
				return n;
			}

		return null;
	}*/

}

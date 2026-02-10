package core.communication;

import static elina.Elina.logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import core.Utils;
import core.init.Configuration;
import core.init.ConfigurationException;
import drivers.CommunicationDriver;
import elina.Elina;
import service.Application;
import service.IFuture;

/**
 * Communication module
 * 
 * @author João Saramago
 * 
 * In revision by Hervé Paulino
 */
public class CommunicationModule {
	
	private static final int DefaultPort = 9090;
	
	
	/**
	 * The representation of the local node
	 */
	private static Node<InetSocketAddress> localNode;
	
	

	private static List<core.communication.Node<?>> nodes = new ArrayList<core.communication.Node<?>>();

	private static Map<UUID, IComEvent> events = new HashMap<UUID, IComEvent>();
	
	public CommunicationModule(Configuration conf)	 throws SocketException, ConfigurationException {
		// Discover local node from the list of nodes
		List<String> myips = this.getMyIps();
		int port;
		try {
			port = Integer.parseInt(conf.getComParams().get("port"));
		}
		catch (NullPointerException e) { 
			port = DefaultPort; // No communication driver was supplied
		}
		
		for (Node node : nodes) {
			node.setNode(new InetSocketAddress(node.getAddr(), node.getPort()));
			InetSocketAddress n = (InetSocketAddress) node.getNode();
			InetAddress inet = n.getAddress();
			if (inet == null)
				throw new SocketException("Host " + n.getHostName()
						+ " not found");

			if ((myips.contains(inet.getHostAddress()) || inet
					.isLoopbackAddress()) && n.getPort() == port) {
				node.setLocal();
				localNode = node;
				
				if (Elina.DEBUG)
					logger.debug(CommunicationModule.class, "Local Node: " + localNode.getAddr() + ":" + localNode.getPort());
			}
		}
		if (localNode == null)
			throw new ConfigurationException("The address of the local node cannot be deduced. " +
					"Either specify a communication adapter or remote the port attribute of the" +
					" local node in the middleware section.");

		
	}

	/**
	 * Método que envia uma mensagem
	 * 
	 * @param msg
	 *            Mensagem
	 */
	public <C> void send(Message<C> msg) {
		CommunicationDriver adapter = CommunicationFactory.createCommunication(msg.getDestination_host());
		adapter.send(msg);
	}

	/**
	 * Método onde os objectos se registam para receber mensagens
	 * 
	 * @param oid
	 *            Identificador
	 * @param event
	 *            Objecto que quer receber mensagens
	 */
	public void registerEvent(UUID oid, IComEvent event) {
		this.events.put(oid, event);
	}

	/**
	 * Método que envia uma mensagem
	 * 
	 * @param content
	 *            Conteudo
	 * @param destination
	 *            Objecto destino
	 * @param sending
	 *            Objecto que envia
	 * @param tag
	 *            Etiqueta da mensagem
	 */
	public <C> void sendMessage(C content, UUID destination, UUID sending,
			MessageTag tag) {
		Message<C> msg = new Message<C>(tag, content, sending);
		msg.setDestination_object(destination);
		CommunicationDriver adapter = CommunicationFactory.createCommunication(msg.getDestination_host());
		adapter.send(msg);
	}

	/**
	 * Método que envia uma mensagem
	 * 
	 * @param content
	 *            Conteudo
	 * @param destination
	 *            Objecto destino
	 * @param sending
	 *            Objecto que envia
	 * @param tag
	 *            Etiqueta da mensagem
	 * @param host
	 *            Nó destino
	 */
	@SuppressWarnings("rawtypes")
	public <C> void sendMessage(C content, UUID destination, UUID sending,
			MessageTag tag, Node host) {
		
		Message<C> msg = new Message<C>(tag, content, sending);
		msg.setDestination_object(destination);
		msg.setDestination_host(host);
		CommunicationDriver adapter = CommunicationFactory.createCommunication(host);
		adapter.send(msg);
	}

	/**
	 * Método que envia uma mensagem
	 * 
	 * @param content
	 *            Conteudo
	 * @param host
	 *            Nó destino
	 * @param sending
	 *            Objecto que envia
	 * @param tag
	 *            Etiqueta da mensagem
	 */
	@SuppressWarnings("rawtypes")
	public <C> void sendMessage(C content, Node host, UUID sending,
			MessageTag tag) {
		Message<C> msg = new Message<C>(tag, content, sending);
		msg.setDestination_host(host);
		CommunicationDriver adapter = CommunicationFactory.createCommunication(host);
		adapter.send(msg);
	}

	/**
	 * Método que envia um aplicação para um nó
	 * 
	 * @param a
	 *            Aplicação
	 * @param n
	 *            Nó destino
	 * @return Futuro que representa a aplicação
	 */
	@SuppressWarnings("rawtypes")
	public IFuture<Void> sendApplication(Application a, Node n) {
		@SuppressWarnings("unchecked")
		RemoteApplicationFuture<Void> out = new RemoteApplicationFuture();
		this.sendMessage(
				new Object[] { a.getAppID(), Utils.toByteArray(a),
						a.getRemoteClassLoader(), a.getClassPath() }, n,
				out.getID(), MessageTag.APPLICATION);
		return out;
	}

	/**
	 * Returna uma lista de endereços IP's V4 e V6 do host local
	 * 
	 * @return Lista de endereços IP
	 * @throws SocketException
	 */
	private List<String> getMyIps() throws SocketException {
		ArrayList<String> out = new ArrayList<String>();

		for (Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
					.hasMoreElements();) {
				out.add(enumIpAddr.nextElement().toString().substring(1));
			}
		}
		return out;
	}

	public static Node<InetSocketAddress> getLocalNode() {
		return localNode;
	}

	public static void addNode(String id, int port) {
		nodes.add(new Node(id,port));	
	}

	public static List<Node<?>> getNodes() {
		return nodes;
	}

	public static  <C> void processMessage(Message<C> msg) {
		if (msg.getDestination_object() != null) {
			events.get(msg.getDestination_object()).processMessage(msg);
		} else {
			Elina.processMessage(msg);
		}		
		
	}

}

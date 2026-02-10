package communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import org.apache.log4j.Logger;

import core.Utils;
import core.communication.CommunicationModule;
import core.communication.Message;
import core.communication.Node;
import core.scheduling.NodeStatisticsManager;
import drivers.CommunicationDriver;
import elina.Elina;

/**
 * 
 * @author João Saramago
 * 
 */
public class NIOCom implements CommunicationDriver, Runnable {

	private static final int BUFFER_SIZE = 100000000;

	private ServerSocketChannel server;
	private Selector selector;
	private ByteBuffer buffer;

	@SuppressWarnings("rawtypes")
	private Node local;



	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void init() {
		if (Elina.DEBUG)
			logger.debug("init");
		try {
			this.local = CommunicationModule.getLocalNode();

			server = ServerSocketChannel.open();
			InetSocketAddress isa = new InetSocketAddress(this.local.getPort());
			server.socket().bind(isa);
			server.configureBlocking(false);

			selector = SelectorProvider.provider().openSelector();

			buffer = ByteBuffer.allocate(BUFFER_SIZE);
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			server.register(selector, SelectionKey.OP_ACCEPT);

			Thread aux = new Thread(this);
			aux.setName("NIO Comm Thread");
			aux.setDaemon(true);
			aux.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		if (Elina.DEBUG)
			logger.debug("end init");
	}

	@Override
	public <C> void send(Message<C> message) {
		if (Elina.DEBUG)
			logger.debug("Send message type " + message.getType().name()
					+ " to " + message.getDestination_host());
		try {
			SocketChannel client = SocketChannel.open();

			client.configureBlocking(true);

			// Kick off connection establishment
			
			System.out.println(message.getDestination_host());
			client.connect((InetSocketAddress) message.getDestination_host().getNode());

			message.setSending_Host(local);
			NodeStatisticsManager.attachStatistics(message);

			byte[] aux = Utils.toByteArray(message);

			ByteBuffer sendBuffer = ByteBuffer.allocate(4 + aux.length);
			sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
			sendBuffer.putInt(aux.length);
			sendBuffer.put(aux);

			sendBuffer.flip();

			client.write(sendBuffer);

			// ByteBuffer data=ByteBuffer.wrap(aux);
			// data.order(ByteOrder.BIG_ENDIAN);
			// client.write(data);

			client.finishConnect();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	public void run() {
		try {
			while (true) {

				selector.select();

				Iterator<SelectionKey> selectedKeys = selector.selectedKeys()
						.iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable()) {
						accept(server, selector);
					} else if (key.isReadable()) {
						read(key, selector);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}

	}

	private void accept(ServerSocketChannel server, Selector selector)
			throws IOException {
		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = server.accept();

		socketChannel.configureBlocking(false);
		// socketChannel.configureBlocking(true);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(selector, SelectionKey.OP_READ);

	}

	// @SuppressWarnings("rawtypes")
	// private Node getClient(SocketChannel s) {
	// Iterator<Node> it = conf.getNodes().iterator();
	//
	// InetSocketAddress remote = (InetSocketAddress)
	// s.socket().getRemoteSocketAddress();
	// while (it.hasNext()) {
	// Node n = it.next();
	// InetSocketAddress i = (InetSocketAddress) n.getNode();
	//
	// if (i.getAddress().equals(remote.getAddress())) {
	// return n;
	//
	// }
	//
	// }
	// return null;
	// }

	private void read(SelectionKey key, Selector select) throws IOException {

		SocketChannel socketChannel = ((SocketChannel) key.channel());
		int numRead = 0;
		try {
			numRead = socketChannel.read(buffer);
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			// key.channel().close();
			socketChannel.close();
			key.cancel();
			return;
		}

		buffer.position(0);

		int size = buffer.getInt();

		if ((size + 4) > buffer.capacity()) {
			ByteBuffer aux = ByteBuffer.allocate(size + 4);
			aux.put(buffer.array());
			buffer = aux;
		}

		if (numRead < (size + 4)) {
			buffer.position(numRead);
		}

		while (numRead < (size + 4)) {

			try {
				int r = socketChannel.read(buffer);
				numRead += r;
			} catch (IOException e) {
				logger.error(e);
				e.printStackTrace();
				key.cancel();
				socketChannel.close();
				return;
			}
		}

		buffer.position(4);
		byte[] aux = new byte[size];

		buffer.get(aux);
		// buffer.compact();

		Message<?> msg = (Message<?>) Utils.toObject(aux);

		buffer.clear();

		if (Elina.DEBUG)
			logger.debug("Read message type " + msg.getType().name() + " from "
					+ msg.getSending_host());

		NodeStatisticsManager.getStatistics(msg);

		CommunicationModule.processMessage(msg);
		


	}


}

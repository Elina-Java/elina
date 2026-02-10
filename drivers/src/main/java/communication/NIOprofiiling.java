package communication;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import core.Utils;
import core.communication.CommunicationModule;
import core.communication.IComEvent;
import core.communication.Message;
import core.communication.Node;
import core.scheduling.NodeStatisticsManager;
import elina.Elina;


/**
 * 
 * @author João Saramago
 * 
 */
public class NIOprofiiling extends NIOCom {

	private static final int BUFFER_SIZE = 100000000;

	private ServerSocketChannel server;
	private Selector selector;
	private ByteBuffer buffer;
	
	@SuppressWarnings("rawtypes")
	private Node local;

	private Map<UUID, IComEvent> events = new HashMap<UUID, IComEvent>();
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	private DataOutputStream profiling;
	

	@Override
	public void init() {
		super.init();
			try {
				profiling=new DataOutputStream(new FileOutputStream("NIOprofiiling.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}


	@Override
	public <C> void send(Message<C> message) {
		if(Elina.DEBUG)
			logger.debug("Send message type "+message.getType().name()+" to "+message.getDestination_host());
		try {
			SocketChannel client = SocketChannel.open();
			
			client.configureBlocking(true);
			
			// Kick off connection establishment
			client.connect((InetSocketAddress)message.getDestination_host().getNode());
			
			message.setSending_Host(local);
			NodeStatisticsManager.attachStatistics(message);
			
			
			
			
			long time_seri = System.nanoTime();
			byte[] aux = Utils.toByteArray(message);
			time_seri=System.nanoTime()-time_seri;
			
			
			
			long time_send = System.nanoTime();
			ByteBuffer sendBuffer=ByteBuffer.allocate(4+aux.length);
			sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
			sendBuffer.putInt(aux.length);
			sendBuffer.put(aux);
			
			sendBuffer.flip();
			
			
			
			
			client.write(sendBuffer);
			
			
//			ByteBuffer data=ByteBuffer.wrap(aux);
//			data.order(ByteOrder.BIG_ENDIAN);
//			client.write(data);
			
			
			
			client.finishConnect();
			client.close();
			time_send=System.nanoTime()-time_send;
			
			profiling.writeUTF("SEND:\tsize:\t"+aux.length+"\tserialize\t"+time_seri+"\tsend\t"+time_send+"\n");
			
		} catch (IOException e) {
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
//		socketChannel.configureBlocking(true);
		
		

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(selector, SelectionKey.OP_READ);

	}

//	@SuppressWarnings("rawtypes")
//	private Node getClient(SocketChannel s) {
//		Iterator<Node> it = conf.getNodes().iterator();
//		
//		InetSocketAddress remote = (InetSocketAddress) s.socket().getRemoteSocketAddress();
//		while (it.hasNext()) {
//			Node n = it.next();
//			InetSocketAddress i = (InetSocketAddress) n.getNode();
//
//			if (i.getAddress().equals(remote.getAddress())) {
//				return n;
//
//			}
//
//		}
//		return null;
//	}

	private void read(SelectionKey key, Selector select) throws IOException {

		SocketChannel socketChannel = ((SocketChannel) key.channel());
		long time_read = System.nanoTime();
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
//			key.channel().close();
			socketChannel.close();
			key.cancel();
			return;
		}

		
		buffer.position(0);
		
		int size=buffer.getInt();
		
		if((size+4)>buffer.capacity()){
			
			
			if(Elina.DEBUG)
				logger.debug("Read message: redimensioning from"+buffer.capacity()+" to "+size);
			
			
			ByteBuffer aux=ByteBuffer.allocate(size+4);
			aux.put(buffer.array());
			buffer=aux;
		}
		
		if(numRead<(size+4)){
			buffer.position(numRead);
		}
		
		time_read=System.nanoTime()-time_read;
		long time_read2 = System.nanoTime();
		
		while(numRead<(size+4)){
			
			if(Elina.DEBUG)
				logger.debug("Read n: " + numRead + " bytes ");
			
			try {
				int r= (int) socketChannel.read(buffer); 
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
		time_read2=System.nanoTime()-time_read2;
		// buffer.compact();
		long time_unseri = System.nanoTime();
		Message<?> msg=(Message<?>) Utils.toObject(aux);
		time_unseri=System.nanoTime()-time_unseri;
		
		profiling.writeUTF("RECEIVE:\tsize:\t"+aux.length+"\tunserialize\t"+time_unseri+"\tread\t"+time_read +" + " + time_read2 + "\n");
		
		buffer.clear();
		
		if(Elina.DEBUG)
			logger.debug("Read message type "+msg.getType().name()+" from "+msg.getSending_host());
		
		NodeStatisticsManager.getStatistics(msg);
		
		CommunicationModule.processMessage(msg);
		

	}

	

}

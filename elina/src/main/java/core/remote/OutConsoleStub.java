package core.remote;

import static elina.Elina.logger;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class OutConsoleStub implements IRemoteOutConsole,Serializable {

	private static final long serialVersionUID = 1L;
	private String host;
	private int port;

	transient private SocketChannel client;
	
	public OutConsoleStub(IRemoteOutConsole myOutConsole) {
		this.host=myOutConsole.getIP();
		this.port=myOutConsole.getPort();
	}

	@Override
	public void print(String s) {
		if(client==null){
			try {
				client = SocketChannel.open();
				client.configureBlocking(true);
				client.connect(new InetSocketAddress(InetAddress.getByName(host),port));
			} catch (IOException e) {
				logger.error(OutConsoleStub.class, e.getMessage(),e);
			}
		}
		
		
		try {
			
			ByteBuffer sendBuffer=ByteBuffer.wrap(s.getBytes());
			client.write(sendBuffer);
//			Socket socket = new Socket();
//			socket.setReuseAddress(true);
//			socket.setTcpNoDelay(true);
//			socket.connect(new InetSocketAddress(InetAddress.getByName(host),port));
//			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//		
//			dos.writeUTF(s);
//			dos.flush();
//			
//			dos.close();
//			socket.close();
		} catch (IOException e) {
			logger.error(OutConsoleStub.class, e.getMessage(),e);
		}
	}

	@Override
	public String getIP() {
		return this.host;
	}

	@Override
	public int getPort() {
		return this.port;
	}

}

package core.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import service.RemoteException;

public class InConsoleStub implements IRemoteInConsole,Serializable {

	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;

	public InConsoleStub(IRemoteInConsole myInConsole) {
		this.ip=myInConsole.getIP();
		this.port=myInConsole.getPort();
	}

	@Override
	public int read() throws IOException,RemoteException {
		try {
			Socket socket = new Socket();
			socket.setReuseAddress(true);
			socket.connect(new InetSocketAddress(InetAddress.getByName(ip),port));
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
		
			dos.writeInt(1);
			dos.flush();
			
			int out = dis.readInt();
			
			dos.close();
			socket.close();
			return out;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteException(e.getLocalizedMessage());
		}
	}

	@Override
	public RemoteRead read(int off, int len) throws IOException,RemoteException {
		try {
			Socket socket = new Socket(InetAddress.getByName(ip),port);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			ObjectInputStream ois = new ObjectInputStream(dis);
		
			dos.writeInt(2);
			dos.writeInt(off);
			dos.writeInt(len);
			dos.flush();
			
			RemoteRead out = (RemoteRead)ois.readObject();
			
			dos.close();
			socket.close();
			return out;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteException(e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getIP() {
		return this.ip;
	}

	@Override
	public int getPort() {
		return this.port;
	}

}

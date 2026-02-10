package core.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RemoteClassLoaderStub implements IRemoteClassLoader, Serializable {

	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;

	public RemoteClassLoaderStub(IRemoteClassLoader classLoader) {
		this.ip = classLoader.getIp();
		this.port = classLoader.getPort();
	}

	@Override
	public byte[] loadClass(String name) {
		try {
			Socket s = new Socket();
			s.setReuseAddress(true);
			s.connect(new InetSocketAddress(InetAddress.getByName(ip), port));
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			dos.writeUTF(name);
			dos.flush();

			int size = dis.readInt();

			if (size == -1) {
				return null;
			} else {
				byte[] data = new byte[size];
				dis.readFully(data);
				dis.close();
				dos.close();
				s.close();
				return data;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public int getPort() {
		return port;
	}

}

package core.remote.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.UUID;

import core.CLoader;


public class UUIDInputStream extends ObjectInputStream {

	private UUID id;

	public UUIDInputStream(InputStream in, UUID id) throws IOException {
		super(in);
		this.id = id;

	}

	@Override
	public int read(byte[] buf, int off, int len) throws IOException {
		//CLoader.registerThread(Thread.currentThread(), this.id);
		ClassLoader cl = CLoader.getClassLoader(id);
		if(cl!=null)
			Thread.currentThread().setContextClassLoader(cl);
		return super.read(buf, off, len);
	}

	@Override
	public int read() throws IOException {
		//CLoader.registerThread(Thread.currentThread(), this.id);
		ClassLoader cl = CLoader.getClassLoader(id);
		if(cl!=null)
			Thread.currentThread().setContextClassLoader(cl);
		return super.read();
	}
}

package core.remote.rmi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class UUIDOutputStream extends ObjectOutputStream {

	public UUIDOutputStream(OutputStream out, UUID uuid) throws IOException {
		super(out);
	}

}

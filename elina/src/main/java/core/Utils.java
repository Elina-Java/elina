package core;




import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;



public class Utils {

	
	
	
	
	public static Object toObject(byte[] bytes) throws EOFException {
		return toObject(bytes, ClassLoader.getSystemClassLoader());
	}
	
	public static Object toObject(byte[] bytes,ClassLoader cl) throws EOFException {
//		Thread.currentThread().setContextClassLoader(cl);
//		
//		DeserializationContext dctx = new DeserializationContext();
//		return dctx.deserialize(ByteBuffer.wrap(bytes));
		
		Object object = null;
		try {
			object = new CustomObjectInputStream(new java.io.ByteArrayInputStream(bytes),cl).readObject();
		} catch (EOFException e) {
			throw e;
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch (java.lang.ClassNotFoundException cnfe) {

			cnfe.printStackTrace();
		}
		return object;
	}
	
	public static byte[] toByteArray(Object obj) {
//		SerializationContext ctx = new SerializationContext();
//		ByteBuffer buffer = ByteBuffer.allocate(1024);
//		ctx.serialize(obj, buffer);
//		buffer.flip();
//		
//		return buffer.array();
		
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			bos.close();
			bytes = bos.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}
}

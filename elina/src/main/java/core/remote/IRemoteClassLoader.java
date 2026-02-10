package core.remote;



public interface IRemoteClassLoader {
	
	public byte[] loadClass(String name);

	public String getIp();
	public int getPort();

}

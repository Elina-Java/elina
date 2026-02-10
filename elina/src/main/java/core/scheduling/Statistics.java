package core.scheduling;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;


public class Statistics implements Externalizable{

	private static final long serialVersionUID = 1L;

	//Processador
	private int nProcessors;
	private double systemLoadAverage;
	
	//File System
	private long freeSpace;
	private long totalSpace;
	private long usableSpace;
	
	//memória
	private long freeMemory;
	private long maxMemory;
	private long totalMemory;
	
	Statistics(){
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		
		nProcessors=operatingSystemMXBean.getAvailableProcessors();
		systemLoadAverage=operatingSystemMXBean.getSystemLoadAverage();
		
		File f = new File(".");
		freeSpace=f.getFreeSpace();
		totalSpace=f.getTotalSpace();
		usableSpace=f.getUsableSpace();
		
		
		Runtime r =Runtime.getRuntime();
		freeMemory= r.freeMemory();
		maxMemory=r.maxMemory();
		totalMemory=r.totalMemory();
	}

	public int getnProcessors() {
		return nProcessors;
	}

	public double getSystemLoadAverage() {
		return systemLoadAverage;
	}

	public long getFreeSpace() {
		return freeSpace;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		
		out.writeInt(nProcessors);
		out.writeDouble(systemLoadAverage);
		out.writeLong(freeSpace);
		out.writeLong(totalSpace);
		out.writeLong(usableSpace);
		
		out.writeLong(freeMemory);
		out.writeLong(maxMemory);
		out.writeLong(totalMemory);
		
		
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		
		nProcessors=in.readInt();
		systemLoadAverage=in.readDouble();
		freeSpace=in.readLong();
		totalSpace=in.readLong();
		usableSpace=in.readLong();
		
		freeMemory=in.readLong();
		maxMemory=in.readLong();
		totalMemory=in.readLong();
	}
	
}

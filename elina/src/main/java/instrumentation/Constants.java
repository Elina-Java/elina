package instrumentation;

import org.objectweb.asm.Opcodes;

public interface Constants {
	
	// Processor Template Files

	static final String CLASS_TEMPLATE_FILE = "/class_template.txt";
	static final String METHOD_TEMPLATE_FILE = "/method_template.txt";
	static final String MEM_METHOD_TEMPLATE_FILE = "/mem_method_template.txt";
	static final String DISTRED_METHOD_TEMPLATE_FILE = "/distRed_method_template.txt";
	static final String DISTREDTASK_TEMPLATE_FILE = "/reddisttask_template.txt";
	static final String WEB_SERVICE_STUB_TEMPLATE = "/web_service_stub_template.txt";
	static final String WEB_SERVICE_METHOD_TEMPLATE = "/web_service_method_template.txt";
	
	// Class Names
	static final String SERVICE = "service.Service";
	static final String IRECONFIGURABLE = "service.IReconfigurable";
	static final String ACTIVE_SERVICE = "service.ActiveService";
	static final String DIST_RED_SERVICE = "service.aggregator.DistRedService";
	static final String DISTRIBUTION = "core.collective.Distribution";
	static final String ABSTRACT_DISTRIBUTION = "core.collective.AbstractDistribution";
	static final String REDUCTION = "core.collective.Reduction";
	static final String ABSTRACT_REDUCTION = "core.collective.AbstractReduction";
	static final String PARTITIONED_MEM_SERVICE = "service.aggregator.PartitionedMemService";
	static final String ABSTRACT_PARTITIONED_MEM_SERVICE = "service.aggregator.AbstractPartitionedMemService";
	static final String MAP_PARTITIONED_MEM_SERVICE = "service.aggregator.MapPartitionedMemService";
	static final String PARTITIONER = "service.aggregator.IPartitioner";
	static final String SERVICE_INTERFACE = "service.IService";
	static final String SERVICE_SCHEDULER = "service.aggregator.IServiceScheduler";
	static final String SERVICE_POOL = "service.aggregator.ServicePool";
	static final String SERVICE_STUB = "service.ServiceStub";
	static final String EXTERNAL_SERVICE_STUB = "service.ExternalServiceStub";
	static final String SERVICE_IDENTIFIER = "service.ServiceIdentifier";
	static final String LEVEL = "core.Level";
	
	// Agent 
	static final String CALLBYVALUE_ANNOTATION = "Linstrumentation/definitions/Copy;";
	public static final String ATOMIC_ANNOTATION = "Linstrumentation/definitions/Atomic;";
	public static final String TASK_ANNOTATION = "Linstrumentation/definitions/Task;";
	
	public static final String ISERVICE = "Lservice/IService;";
	public static final String IFUTURE = "Lservice/IFuture;";
	public static final String ITASK = "Lservice/ITask;";
	public static final String IFUTURE_DESC = "service/IFuture";
//	public static final String SERVICE = "service/Service";
//	public static final String ACTIVE_SERVICE = "service/ActiveService";
//	public static final String SERVICE_STUB = "service/ServiceStub";
	
	public static final int VERSION=Opcodes.V1_7;
	
	// ZIB
	static final Object ZIB_SERVICE = "zib.runtime.Service";


}


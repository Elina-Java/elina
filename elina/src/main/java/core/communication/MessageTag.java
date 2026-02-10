package core.communication;

/**
 * 
 * Tags permitidas para as mensagens trocadas entre o middleware. Será
 * necessário criar rotinas de atendimento para processar mensagens com
 * determinada tag.
 * 
 * @author Diogo Mourão
 * @author João Saramago
 * 
 */

public enum MessageTag {
	ADDTASK, 
	APPLICATION,
	SHUTDOWN_APP,
	APP_END,
	
	REMOTE_FUTURE,
	TASK_RESULT,
	
	FUTURE_OP,
	OP_RESULT,
	
	PLAIN_MESSAGE, 
	
	BARRIER_REGISTER, 
	BARRIER_AWAIT, 
	BARRIER_AWAKE, 
}

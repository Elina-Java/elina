package core.taskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;





public class TaskRegistry<F> {

	/**
	 * 
	 */
	private Map<UUID, List<F>> activetasks = new HashMap<UUID, List<F>>();
	
	/**
	 * 
	 */
	private Map<F, UUID> activestasks = new HashMap<F, UUID>();
	
	public  void register(UUID clientId, F task) {
		
		List<F> tasks = activetasks.get(clientId);
		if (tasks == null)
			tasks = new ArrayList<F>();

		tasks.add(task);

		activetasks.put(clientId, tasks);
		activestasks.put(task, clientId);
	}
	
	public List<F> removeClient(UUID client) {
		return activetasks.remove(client);
	}
	
	public <R> void removeTask(F task) {
		try {
			activetasks.get(activestasks.remove(task)).remove(task);
		} catch (NullPointerException e) {
			// Ignore exception
		}
	}
	
}

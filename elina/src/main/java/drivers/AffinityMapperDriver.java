package drivers;

public interface AffinityMapperDriver {

	/**
	 * Sets the desired affinities for the worker pool managed by taskManager.
	 * @param taskManager TaskExecutorDriver object responsible for the worker pool
	 */
	void setAffinities(TaskExecutorDriver taskManager);
}

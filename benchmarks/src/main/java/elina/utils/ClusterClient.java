package elina.utils;




public abstract class ClusterClient /* extends ActiveService */ {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final int EXECS;
	
	protected final String applicationName;
	
	protected Evaluation eval;
	
	public ClusterClient(String applicationName, int nExecs, int nSites) {
		this.EXECS = nExecs;
		this.applicationName = applicationName + "_" + nSites;
		this.eval = new Evaluation(EXECS); 
	}
}

package service;






/**
 * 
 * @author Diogo Mourão
 *
 */

public interface IMonitor {

	void beginAtomic();

	void endAtomic();
	
	ICondition newCondition(ConditionCode conditionTest);
}

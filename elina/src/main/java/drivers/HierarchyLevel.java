package drivers;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import flexjson.JSON;

public class HierarchyLevel {

	public static final int L1 = 1;
	public static final int L2 = 2;
	public static final int L3 = 3;
	
	@JSON(include=true)
	public long size;
	@JSON(include=true)
	public int cacheLineSize;
	@JSON(include=true)
	public int[][] siblings;
	@JSON(include=true)
	/**
	 * HierarchyLevel object representative of the child level of the current level.
	 */
	public HierarchyLevel child;
	
	
	//Required for json to work
	public HierarchyLevel(){}
	
	public HierarchyLevel(long size, int[][] siblings, HierarchyLevel child)
	{
		this.size=size;
		this.siblings=siblings;
		this.child=child;
	}
	
	//@JSON(include=false)
	/**
	 * Returns an array of arrays containing the sibling cores sharing memory on the current level.
	 * @return Array of arrays of sibling cores in the current level
	 */
	public int[][] getSiblings()
	{
		return siblings;
	}
	
	//@JSON(include=false)
	/**
	 * Returns the size (in bytes) of the current memory level.
	 * @return Memory size (in bytes)
	 */
	public long getSize()
	{
		return size;
	}
	
	//@JSON(include=false)
	/**
	 * Returns the size (in bytes) of a coherency line in the current memory level.
	 * @return Coherency line size (in bytes)
	 */
	public int getCacheLineSize()
	{
		return cacheLineSize;
	}
	
	/**
	 * Returns the child level of the current memory level.
	 * @return Child level
	 */
	@JSON(include=false)
	public HierarchyLevel getChildren()
	{
		return child;
	}
	
	/**
	 * Returns the Nth descendant level of the current memory level.
	 * @param N number of the descendant level
	 * @return The Nth descendant level
	 */
	@JSON(include=false)
	public HierarchyLevel getLevel(int level)
	{
		Stack<HierarchyLevel> stack = new Stack<HierarchyLevel>();
		HierarchyLevel current = this;
		while(current!=null)
		{
			stack.add(current);
			current = current.getChildren();
		}
		current = null;
		for(int i=0;i<level;i++)
			current=stack.pop();
		return current;
	}
	
	@JSON(include=false)
	/**
	 * Returns the bottom-most shared memory level, starting on the current level.
	 * @return The Bottom-most shared memory level
	 */
	public HierarchyLevel getBottomUpFirstShared()
	{
		HierarchyLevel previous = null;
		HierarchyLevel current = this;
		while(current!=null && current.getSiblings()[0].length>1)
		{
			previous=current;
			current = current.getChildren();
		}
		return previous;
	}
	
	/**
	 * Returns the first dedicated chain in the hierarchy.
	 * 
	 * Ex1.
	 * 
	 *   				L2
	 *   			 ___|___
	 *   			|		|
	 *   			L1		L1
	 *   
	 *   {L1} is the first dedicated chain
	 *   
	 * Ex2. 
	 *       
	 *       
	 *       			 L3
	 *   			  ___|___
	 *   			 |		 |
	 *   			 L2		 L2
	 *   			 |		 |
	 *   			 L1		 L1
	 *   
	 *   {L2,L1} is the first dedicated chain
	 * @return
	 */
	@JSON(include=false)
	public HierarchyLevel getDedicatedLevels()
	{
		List<HierarchyLevel> list = new LinkedList<HierarchyLevel>();
		HierarchyLevel current = this;
		
		while(current!=null)
		{
			list.add(0,current);
			current = current.getChildren();
		}
		//in fact, it is now a "previous" from a semantical point of view
		current = null;
		for(HierarchyLevel lvl: list)
		{
			//null means "dedicated" but without providing any further information
			if(lvl.siblings!=null && lvl.siblings[0].length > 1)
				break;
			else
				current = lvl;
		}
		return current;
	}
}

package drivers;


public interface HierarchyReadDriver {
	
	/**
	 * Returns a HierarchyLevel object representative of the root of the memory hierarchy.
	 * @return Hierarchy root
	 */
	HierarchyLevel getHierarchyRoot();

	HierarchyLevel getHierarchyRoot(String filename);

}

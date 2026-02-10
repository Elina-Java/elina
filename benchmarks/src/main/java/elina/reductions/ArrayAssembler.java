package elina.reductions;


import core.collective.AbstractReduction;

/**
 * Assemble multiple partial arrays into a single one
 * 
 * @author Eduardo Marques
 * @author Herve Paulino
 *
 * @param <T>
 */
public class ArrayAssembler<T> extends AbstractReduction<T[]> {
	
	public T[] reduce(T[][] results) {
		int last = results.length-1;
		
		@SuppressWarnings("unchecked")
		T[] result = (T[]) new Object[results[0].length*last +results[last].length];
			
		for (int count = 0, i = 0; i < results.length; i++)
			for(int j = 0; j < results[i].length; j++) 
				result[count++] = results[i][j];

		return result;
	}
	
	
}
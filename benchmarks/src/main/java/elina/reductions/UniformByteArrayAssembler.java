package elina.reductions;

import core.collective.AbstractReduction;

/**
 * Assemble multiple partial byte arrays (all with the same length, except, possibility, the last one) into a single one
 * 
 * @author Eduardo Marques
 * @author Herve Paulino
 */
public class UniformByteArrayAssembler extends AbstractReduction<byte[]> {
	
	public byte[] reduce(byte[][] results) {
		int last = results.length-1;
		byte[] result = new byte[results[0].length*last +results[last].length];
			
		for (int count = 0, i = 0; i < results.length-1; i++)
			for(int j = 0; j < results[i].length; j++) 
				result[count++] = results[i][j];

		return result;
	}

}
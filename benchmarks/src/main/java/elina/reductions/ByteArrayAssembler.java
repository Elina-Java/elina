package elina.reductions;

import core.collective.AbstractReduction;

/**
 * Assemble multiple partial byte arrays, with possibly different lengths, into a single one
 * 
 * @author Herve Paulino
 */
public class ByteArrayAssembler extends AbstractReduction<byte[]> {
	
	public byte[] reduce(byte[][] results) {			
		int size = 0;
		for (int i = 0 ; i < results.length; i++) {
			if (results[i] == null)
				System.err.println(i  + " --- nulll " );	
	//		else
		//		System.out.println(i  + " --- " + results[i].length);	
		//	
			size += results[i].length;
		}
		
		
		byte[] result = new byte[size];
			
		for (int count = 0, i = 0; i < results.length-1; i++)
			for(int j = 0; j < results[i].length; j++) 
				result[count++] = results[i][j];

		return result;
	}

}
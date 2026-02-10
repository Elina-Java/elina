package elina.reductions;

import core.collective.AbstractReduction;


public class MandelReduce extends AbstractReduction<byte[][]>{

	
	public byte[][] reduce(byte[][][] array) {
		int height = 0;
		for(int i = 0; i < array.length; i++)
			height += array[i].length;
		
		int width = array[0][0].length;
		
		byte[][] data = new byte[height][width];
		int count = 0;
		for(int i = 0; i < array.length; i++)
			for(int j = 0; j < array[i].length; j++)
				data[count++] = array[i][j];
		
		return data;
	}



}

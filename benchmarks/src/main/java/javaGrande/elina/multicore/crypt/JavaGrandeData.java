package javaGrande.elina.multicore.crypt;

import java.util.Random;

public final class JavaGrandeData {
	
	public static int sizes[] = new int[] { 
			3000000, 20000000, 50000000 // reference JavaGrande configurations (A, B, C)
		};
	public static int NUMBER_OF_PROBLEMS = sizes.length;
	
	
	public int[] Z;
	public int[] DK;
	
	private short[] symmKey;

	public JavaGrandeData() {
		if (symmKey == null)
			this.symmKey = getKey();

		if (Z == null) {
			this.Z = new int[52];
			calcEncryptKey(); // Init Z
		}

		if (DK == null) {
			this.DK = new int[52];
			calcDecryptKey(); // Init DK
		}
	}

	
	public static short [] getKey()
	{
		Random rndnum = new Random(136506717L);  // Create random number generator.
		
		
		short [] key = new short [8];  // User key has 8 16-bit shorts.
		
		for (int i = 0; i < 8; i++)
		{
			// Again, the random number function returns int. Converting
			// to a short type preserves the bit pattern in the lower 16
			// bits of the int and discards the rest.

			key[i] = (short) rndnum.nextInt();
		}

		// Compute encryption and decryption subkeys.

		return key;
	}
	
	
	public static byte [] getText(int size)
	{
		byte [] text = new byte[size];
		
		// Fill plain1 with "text."
		for (int i = 0; i < size; i++)
		{
			text[i] = (byte) i; 

			// Converting to a byte
			// type preserves the bit pattern in the lower 8 bits of the
			// int and discards the rest.
		}
		
		return text;
	}
	
	private void calcEncryptKey() {
		int j; // Utility variable.

		for (int i = 0; i < 52; i++)
			// Zero out the 52-int Z array.
			Z[i] = 0;

		for (int i = 0; i < 8; i++) // First 8 subkeys are userkey itself.
		{
			Z[i] = symmKey[i] & 0xffff; // Convert "unsigned"
			// short to int.
		}

		// Each set of 8 subkeys thereafter is derived from left rotating
		// the whole 128-bit key 25 bits to left (once between each set of
		// eight keys and then before the last four). Instead of actually
		// rotating the whole key, this routine just grabs the 16 bits
		// that are 25 bits to the right of the corresponding subkey
		// eight positions below the current subkey. That 16-bit extent
		// straddles two array members, so bits are shifted left in one
		// member and right (with zero fill) in the other. For the last
		// two subkeys in any group of eight, those 16 bits start to
		// wrap around to the first two members of the previous eight.

		for (int i = 8; i < 52; i++) {
			j = i % 8;
			if (j < 6) {
				Z[i] = ((Z[i - 7] >>> 9) | (Z[i - 6] << 7)) // Shift and
															// combine.
				& 0xFFFF; // Just 16 bits.
				continue; // Next iteration.
			}

			if (j == 6) // Wrap to beginning for second chunk.
			{
				Z[i] = ((Z[i - 7] >>> 9) | (Z[i - 14] << 7)) & 0xFFFF;
				continue;
			}

			// j == 7 so wrap to beginning for both chunks.

			Z[i] = ((Z[i - 15] >>> 9) | (Z[i - 14] << 7)) & 0xFFFF;
		}
	}

	/*
	 * calcDecryptKey
	 * 
	 * Builds the 52 16-bit encryption subkeys DK[] from the encryption- subkeys
	 * Z[]. DK[] is a 32-bit int array holding 16-bit values as unsigned.
	 */

	private void calcDecryptKey() {
		int j, k; // Index counters.
		int t1, t2, t3; // Temps to hold decrypt subkeys.

		t1 = inv(Z[0]); // Multiplicative inverse (mod x10001).
		t2 = -Z[1] & 0xffff; // Additive inverse, 2nd encrypt subkey.
		t3 = -Z[2] & 0xffff; // Additive inverse, 3rd encrypt subkey.

		DK[51] = inv(Z[3]); // Multiplicative inverse (mod x10001).
		DK[50] = t3;
		DK[49] = t2;
		DK[48] = t1;

		j = 47; // Indices into temp and encrypt arrays.
		k = 4;
		for (int i = 0; i < 7; i++) {
			t1 = Z[k++];
			DK[j--] = Z[k++];
			DK[j--] = t1;
			t1 = inv(Z[k++]);
			t2 = -Z[k++] & 0xffff;
			t3 = -Z[k++] & 0xffff;
			DK[j--] = inv(Z[k++]);
			DK[j--] = t2;
			DK[j--] = t3;
			DK[j--] = t1;
		}

		t1 = Z[k++];
		DK[j--] = Z[k++];
		DK[j--] = t1;
		t1 = inv(Z[k++]);
		t2 = -Z[k++] & 0xffff;
		t3 = -Z[k++] & 0xffff;
		DK[j--] = inv(Z[k++]);
		DK[j--] = t3;
		DK[j--] = t2;
		DK[j--] = t1;
	}

	/*
	 * inv
	 * 
	 * Compute multiplicative inverse of x, modulo (2**16)+1 using extended
	 * Euclid's GCD (greatest common divisor) algorithm. It is unrolled twice to
	 * avoid swapping the meaning of the registers. And some subtracts are
	 * changed to adds. Java: Though it uses signed 32-bit ints, the
	 * interpretation of the bits within is strictly unsigned 16-bit.
	 */

	private int inv(int x) {
		int t0, t1;
		int q, y;

		if (x <= 1) // Assumes positive x.
			return (x); // 0 and 1 are self-inverse.

		t1 = 0x10001 / x; // (2**16+1)/x; x is >= 2, so fits 16 bits.
		y = 0x10001 % x;
		if (y == 1)
			return ((1 - t1) & 0xFFFF);

		t0 = 1;
		do {
			q = x / y;
			x = x % y;
			t0 += q * t1;
			if (x == 1)
				return (t0);
			q = y / x;
			y = y % x;
			t1 += q * t0;
		} while (y != 1);

		return ((1 - t1) & 0xFFFF);
	}

	

	
	public static void validate(byte[] plain1, byte[] plain2) {
		for (int i = 0; i < plain1.length; i++) {
			if (plain1[i] != plain2[i]) {
				System.out.println("Validation failed " +  plain1[i] + "  " + plain2[i]);
				break;
			}
		}
	}
}

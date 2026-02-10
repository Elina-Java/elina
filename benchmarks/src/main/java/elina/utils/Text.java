package elina.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Text implements  Iterator<String>, Iterable<String> {

	private final File file;
	private StringTokenizer tokenizer;
	
	public Text(File file, int offset, int nBytes) throws IOException {
		if (!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());
		this.file = file;
		read(offset, nBytes);	
	}
	
	public Text(String fileName, int offset, int nBytes) throws IOException {
		this(new File(fileName), offset, nBytes);
	}
	
	public Text(String fileName) throws IOException {
		this(new File(fileName));
	}
	
	public Text(File file) throws IOException {
		this(file, 0, (int) file.length());
	}
	
	
	private void read(int offset, int nBytes) throws IOException {		
		try {
			FileInputStream in = new FileInputStream(file);
			
		if ((offset + nBytes) > file.length())
			nBytes = (int) (file.length() - offset);
		byte[] text = new byte[nBytes];
		in.read(text, offset, nBytes);
		this.tokenizer = new StringTokenizer(new String(text));
		in.close();
		} catch (FileNotFoundException e) {
			// Cannot happen
			e.printStackTrace();
		}
	}


	@Override
	public Iterator<String> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return tokenizer.hasMoreTokens();
	}

	@Override
	public String next() {
		return this.tokenizer.nextToken();
	}

	@Override
	public void remove() {		
	}
}

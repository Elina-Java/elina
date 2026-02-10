package elina.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

@SuppressWarnings("restriction")
public class StubLoader extends ClassLoader {
	
	// Given a filename, read the entirety of that file from disk
	// and return it as a byte array.
	private byte[] getBytes(String filename) throws IOException {
		// Find out the length of the file
		File file = new File(filename);
		long len = file.length();

		// Create an array that's just the right size for the file's
		// contents
		byte raw[] = new byte[(int) len];

		// Open the file
		FileInputStream fin = new FileInputStream(file);

		// Read all of it into the array; if we don't get all,
		// then it's an error.
		int r = fin.read(raw);
		if (r != len)
			throw new IOException("Can't read all, " + r + " != " + len);

		// Don't forget to close the file!
		fin.close();

		// And finally return the file contents as an array
		return raw;
	}

	// Spawn a process to compile the java source code file
	// specified in the 'javaFile' parameter. Return a true if
	// the compilation worked, false otherwise.
	private void compile(String javaFile) throws IOException {
		// Let the user know what's going on
//		System.err.println("CCL: Compiling " + javaFile + "...");

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		javaCompiler.run(null, null, null, javaFile,
						"-implicit:class",
						"-cp", System.getProperty("user.dir") + File.pathSeparator + 
								System.getProperty("java.class.path") + File.pathSeparator + 
								getEclipseClassPaths());
	}

	// The heart of the ClassLoader -- automatically compile
	// source as necessary when looking for class files
	public Class loadClass(String fullyQualfiedClassName, boolean resolve) throws ClassNotFoundException {
		// Our goal is to get a Class object

		// First, see if we've already dealt with this one
		Class clas = findLoadedClass(fullyQualfiedClassName);

//		System.err.println("findLoadedClass(" + fullyQualfiedClassName + "): " + clas);

		// Create a pathname from the class name
		// E.g. java.lang.Object => java/lang/Object
		String fileStub = Class.forName(fullyQualfiedClassName).getPackage().toString().replace(".", File.separator); 

		// Build objects pointing to the source code (.java) and object
		// code (.class)
		String javaFilename = fileStub + ".java";
		String classFilename = fileStub + ".class";

		File javaFile = new File(javaFilename);
		File classFile = new File(classFilename);

	//	System.out.println("EXIST JAVA FILE?" + javaFilename + ":" 	+ javaFile.exists());
	//	System.out.println("EXIST CLASS FILE?" + classFilename + ":" + classFile.exists());
	
		// First, see if we want to try compiling. We do if (a) there
		// is source code, and either (b0) there is no object code,
		// or (b1) there is object code, but it's older than the source
		if (javaFile.exists()
				&& (!classFile.exists() || javaFile.lastModified() > classFile
						.lastModified())) {

			try {
				// Try to compile it. If this doesn't work, then
				// we must declare failure. (It's not good enough to use
				// and already-existing, but out-of-date, classfile)
				compile(javaFilename);
				if (!classFile.exists()) 
					throw new ClassNotFoundException("Compilation failed: " + javaFilename);
			} catch (IOException ie) {
				// Another place where we might come to if we fail
				// to compile
				throw new ClassNotFoundException(ie.toString());
			}
		}

		// Let's try to load up the raw bytes, assuming they were
		// properly compiled, or didn't need to be compiled

		try {
			 // read the bytes
  	      byte raw[] = getBytes( classFilename );

  	      // try to turn them into a class
  	      clas = defineClass( fullyQualfiedClassName, raw, 0, raw.length );
//			clas = loadClassWithDependencies(sourceFolder, fullyQualfiedClassName, classFilename);
		} catch (IOException ie) {
			// This is not a failure! If we reach here, it might
			// mean that we are dealing with a class in a library,
			// such as java.lang.Object
		}

		// Maybe the class is in a library -- try loading
		// the normal way
		if (clas == null) {
			clas = findSystemClass(fullyQualfiedClassName);
		}

		// Resolve the class, if any, but only if the "resolve"
		// flag is set to true
		if (resolve && clas != null)
			resolveClass(clas);

		// If we still don't have a class, it's an error
		if (clas == null)
			throw new ClassNotFoundException(fullyQualfiedClassName);

		// Otherwise, return the class
		return clas;
	}

	/*private Class loadClassWithDependencies(String sourceFolder,
			String fullyQualfiedClassName, String classFile) throws IOException {

		// read the bytes
		byte raw[] = getBytes(classFile);

		while (true) {
			try {
				// try to turn them into a class
				Class clas = defineClass(fullyQualfiedClassName, raw, 0, raw.length);
				System.out.println("loaded " + fullyQualfiedClassName);
				return clas;

			} catch (NoClassDefFoundError ie) {
				try {
					String depClassName = ie.getMessage().replace(File.separatorChar, '.');
						byte depRaw[] = getBytes(sourceFolder + File.separator + depClassName.split("\\.")[1] + ".class");
					// System.out.println("loaded " + depClassName + ":" +
					// sourceFolder + File.separator +
					// pathComponents[pathComponents.length-1]);
					defineClass(depClassName, depRaw, 0, depRaw.length);
					System.out.println("loaded " + depClassName);
				} catch (IOException e) {
					e.printStackTrace();
					// Should not occur, the class was automatically generated
				}
			}
		}
	}*/
	
	 private String getEclipseClassPaths() {
	    	String userDir = System.getProperty("user.dir");
	    	// Adding src folder of regular projects
	    	String result = File.pathSeparator + userDir + File.separator + "bin";
	    	// Adding src folder of maven projects
	    	result += File.pathSeparator + userDir + File.separator + "target" + File.separator + "classes";
	    	result += File.pathSeparator + userDir + File.separator + "target" + File.separator + "test-classes";
	    	// Adding src folder for Zib test files
	    	result += File.pathSeparator + userDir + File.separator + "src" + File.separator + "test" + File.separator + "resources" ;
	    	return result;
	    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void listLoadedClasses(StubLoader classLoader) {
		try {
			Field f = StubLoader.class.getDeclaredField("classes");
			f.setAccessible(true);

			System.err.println("Loaded classes");
			for (Class classe : (Vector<Class>) f.get(classLoader))
				System.out.println("\t" + classe);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

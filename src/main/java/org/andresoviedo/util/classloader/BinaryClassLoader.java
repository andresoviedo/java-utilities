package org.andresoviedo.util.classloader;



import java.io.ByteArrayInputStream;

/**
 * Clase que carga una clase a partir de su representación binaria.
 * 
 * @author andresoviedo
 */
public class BinaryClassLoader extends ClassLoader {

	private ByteArrayInputStream is;

	public BinaryClassLoader() {
		super();
	}

	public BinaryClassLoader(ByteArrayInputStream is) {
		super();
		this.is = is;
	}

	public ByteArrayInputStream getIs() {
		return is;
	}

	public void setIs(ByteArrayInputStream is) {
		this.is = is;
	}

	public Class<?> findClass(String name) {
		byte[] b = loadClassData(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String name) {
		byte[] ret = new byte[is.available()];
		is.read(ret, 0, is.available());
		return ret;
	}
}
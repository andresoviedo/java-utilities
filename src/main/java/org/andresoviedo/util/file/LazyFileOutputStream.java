package org.andresoviedo.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Lazy initialized FileOutputStream that generates the file only when the first byte is written.
 * 
 * @author andresoviedo
 * 
 */
public class LazyFileOutputStream extends OutputStream {

	private final File file;
	private final boolean append;
	private final Object streamLock = new Object();

	private boolean streamOpen = false;
	private FileOutputStream oStream;

	public LazyFileOutputStream(String filename) throws FileNotFoundException {
		this(new File(filename), false);
	}

	public LazyFileOutputStream(File file, boolean append) {
		super();
		this.file = file;
		this.append = append;
	}

	public LazyFileOutputStream(String fileName, boolean append) {
		this(new File(fileName), append);
	}

	public File getFile() {
		return file;
	}

	public boolean isAppend() {
		return append;
	}

	/**
	 * This method is the key component of the class, it gets the wrapped FileOutputStream object if already initialized or if not it
	 * generates it in a thread safe way. This kind of implementation allows to call the initialization of the underlying FileOutputStream
	 * object only when needed.
	 * 
	 * @return the wrapped FileOutputStream object
	 * @throws FileNotFoundException
	 *             if the file can't be created
	 */
	protected FileOutputStream outputStream() throws FileNotFoundException {
		if (!streamOpen) {
			synchronized (streamLock) {
				if (!streamOpen) {
					oStream = new FileOutputStream(file, append);
					streamOpen = true;
				}
			}
		}
		return oStream;
	}

	@Override
	public void close() throws IOException {
		super.close();
		if (streamOpen)
			outputStream().close();
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		if (streamOpen)
			outputStream().flush();
	}

	@Override
	public void write(int b) throws IOException {
		outputStream().write(b);
	}
}
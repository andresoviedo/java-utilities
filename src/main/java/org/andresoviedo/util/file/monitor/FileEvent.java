package org.andresoviedo.util.file.monitor;

import java.io.File;
import java.util.EventObject;

/**
 * The event fired by a file monitor.
 * 

 */
public class FileEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 866866690099374843L;

	public static final int FILE_NEW = 1001;

	public static final int FILE_DELETE = 1002;

	public static final int FILE_MODIFY = 1003;

	/**
	 * The event id.
	 */
	private int id;

	/**
	 * The file that changed.
	 */
	private File file;

	/**
	 * Constructs a new file event object.
	 * 
	 * @param source
	 *          the object that fires the event.
	 * @param id
	 *          the event id.
	 * @param file
	 *          the file (or directory) that changed.
	 */
	public FileEvent(Object source, int id, File file) {
		super(source);
		this.id = id;
		this.file = file;
	}

	/**
	 * Returns the file (or directory) which was created, updated or deleted.
	 * 
	 * @return the file (or directory) which was created, updated or deleted.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the event id.
	 * 
	 * @return the event id.
	 */
	public int getId() {
		return id;
	}

	/*
	 * @see java.util.EventObject#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("FileEvent[");
		sb.append("id=").append(id).append(", ");
		sb.append("file=").append(file).append("]");

		return sb.toString();
	}

}
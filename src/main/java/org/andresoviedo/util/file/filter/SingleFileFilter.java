package org.andresoviedo.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A file filter that accepts only the specified file. It can be used, for instance, to check whether a specified file exists in a given
 * directory. Notice that the file name comparison is case-insensitive.
 * 

 */
public class SingleFileFilter implements FileFilter {

	/**
	 * The accepted filename.
	 */
	private String filename;

	/**
	 * Creates a new single file filter.
	 * 
	 * @param filename
	 *          the filename to check.
	 * @throws IllegalArgumentException
	 *           if <code>filename</code> is <code>null</code>.
	 */
	public SingleFileFilter(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException("The file name is null.");
		}
		this.filename = filename;
	}

	/*
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return (pathname.isFile() && pathname.getName().equalsIgnoreCase(filename));
	}

}
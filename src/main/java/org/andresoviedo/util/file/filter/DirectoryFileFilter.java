package org.andresoviedo.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A file filter that accepts only directories.
 * 
 */
public class DirectoryFileFilter implements FileFilter {

	/*
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return pathname.isDirectory();
	}

}
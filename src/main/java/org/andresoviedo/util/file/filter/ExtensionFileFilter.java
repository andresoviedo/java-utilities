package org.andresoviedo.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * A file filter that accepts only files with the specified extensions.
 * 
 */
public class ExtensionFileFilter implements FileFilter {

	/**
	 * The array of accepted extensions.
	 */
	private String[] extensions;

	/**
	 * Creates a new extension file filter with the specified set of accepted extensions.
	 * 
	 * @param extensions
	 *            the set of accepted extensions. If the array is <code>null</code> or empty, all files will be accepted.
	 */
	public ExtensionFileFilter(String[] extensions) {
		this.extensions = extensions;
	}

	/*
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		if (!pathname.isFile()) {
			return false;
		}

		if ((extensions == null) || (extensions.length == 0)) {
			return true;
		}

		String filename = pathname.getName().toLowerCase();
		for (int i = 0; i < extensions.length; i++) {
			if ((extensions[i] != null) && (filename.endsWith(extensions[i].toLowerCase()))) {
				return true;
			}
		}
		return false;
	}

}
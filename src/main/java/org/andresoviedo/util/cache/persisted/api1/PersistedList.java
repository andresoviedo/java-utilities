package org.andresoviedo.util.cache.persisted.api1;

import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

public abstract class PersistedList<T> extends AbstractList<T> {
	private File targetFolder = null;

	private Hashtable<T, File> fileRelation = new Hashtable<T, File>();

	private Vector<T> objectData = new Vector<T>();

	public PersistedList(File targetFile) {
		if (targetFile == null) {
			throw new IllegalArgumentException("The target folder cannot be null");
		}

		targetFolder = targetFile;

		if (targetFile.exists()) {
			if (!targetFile.isDirectory()) {
				throw new IllegalArgumentException("The target folder cannot be an existing archive");
			}
		} else {
			// Create it...
			targetFile.mkdirs();
		}
	}

	public PersistedList(String targetFilePath) {
		this(new File(targetFilePath));
	}

	@Override
	public int size() {
		return objectData.size();
	}

	@Override
	public T get(int index) {
		return objectData.elementAt(index);
	}

	@Override
	public T set(int index, T element) {
		T currentObject = objectData.elementAt(index);
		File currentObjectFile = fileRelation.get(currentObject);

		if (currentObjectFile != null) {
			// Modify stored object
			long originalLastModified = currentObjectFile.lastModified();

			// Remove from the file relation list
			fileRelation.remove(currentObject);

			try {
				writeObject(currentObjectFile, element);
			} catch (IOException ioex) {
				// TODO: log this?
			}

			// Restore the old last modified...
			currentObjectFile.setLastModified(originalLastModified);

			fileRelation.put(element, currentObjectFile);
		}

		return objectData.set(index, element);
	}

	public void synch(T element) {
		int index = indexOf(element);
		set(index, element);
	}

	@Override
	public void add(int index, T element) {
		if (index < size()) {
			throw new UnsupportedOperationException("Elements can only be added at the end of the storage.");
		}

		try {
			File destinationFile = writeObject(element);
			fileRelation.put(element, destinationFile);
		} catch (IOException ioex) {
			// Log this
		}

		objectData.addElement(element);
	}

	@Override
	public T remove(int index) {
		T removedObject = objectData.remove(index);

		File targetFile = fileRelation.remove(removedObject);

		// Try to delete it
		if (targetFile != null) {
			if (!targetFile.delete()) {
				targetFile.deleteOnExit();
			}
		}

		return removedObject;
	}

	void loadObjects() {
		// Free access to the queue
		// Check that there are messages on the queue
		File[] files = targetFolder.listFiles();

		if (files.length == 0) {
			// No messages then wait. Log this?
		} else {
			// Read all the files and process in order

			Comparator<File> lastModifiedFileComparator = new Comparator<File>() {
				public int compare(File f1, File f2) {
					return (int) (f1.lastModified() - f2.lastModified());
				}
			};
			Arrays.sort(files, lastModifiedFileComparator);

			for (int i = 0; i < files.length; i++) {
				File f = files[i];

				try {
					T readedObject = readObject(f);

					if (readedObject != null) {
						objectData.addElement(readedObject);
						fileRelation.put(readedObject, f);
					}
				} catch (IOException ioex) {
					// TODO: log this?
				}
			}
		}
	}

	private File writeObject(T target) throws IOException {
		File f = null;
		String name = (new Long(System.currentTimeMillis())).toString();

		int c = 0;
		do {
			String realName = name;
			if (c > 0) {
				realName += "-" + c;
			}
			c++;
			f = new File(targetFolder, realName);
		} while (f.exists());
		writeObject(f, target);
		return f;
	}

	public abstract T readObject(File targetFile) throws IOException;

	public abstract void writeObject(File targetFile, T targetObject) throws IOException;
}

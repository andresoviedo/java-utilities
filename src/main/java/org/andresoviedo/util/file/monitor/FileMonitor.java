package org.andresoviedo.util.file.monitor;

import java.io.File;
import java.io.FileFilter;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

/**
 * <p>
 * This class monitors a file or a directory and notifies any change from this moment on. It has an internal thread which will poll the
 * file/directory regularly. It detects:
 * <ol>
 * <li>New file in directory (when pointing to a directory).</li>
 * <li>File created (when pointing to a file wich did not exist).</li>
 * <li>File modified.</li>
 * <li>File removed.</li>
 * </ol>
 * File monitor can also scan over subdirectories when it is requested to do so.
 * </p>
 * 
 */
public class FileMonitor implements Runnable {

	/**
	 * The default check period (20 seconds).
	 */
	private static final long DEFAULT_PERIOD = 20000;

	/**
	 * The root file or directory.
	 */
	private File root;

	/**
	 * Indicates whether subdirectories have to be monitored or not.
	 */
	private boolean includeSubdirectories;

	/**
	 * The checking period (in milliseconds).
	 */
	private long period;

	/**
	 * A file filter used to restrict the files that have to be checked.
	 */
	private FileFilter filter;

	/**
	 * The Buffer of files to be checked. The buffer holds information about all files.
	 */
	private Hashtable<File, FileInfo> _vectorFileInfo = new Hashtable<File, FileInfo>();

	/**
	 * The event listener list to hold file listeners.
	 */
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * The thread that performs file monitoring.
	 */
	private Thread thread;

	/**
	 * Indicates if this file monitor's thread has been started or not. Used for internal control.
	 */
	private boolean started;

	/**
	 * Indicates if the initial load has been done. Used for internal control.
	 */
	private boolean initialLoad;

	/**
	 * Constructs a new file monitor.
	 * 
	 * @param root
	 *            the file or directory to monitor.
	 * @param includeSubdirectories
	 *            indicates whether subdirectories have to be also monitored or not.
	 */
	public FileMonitor(File root, boolean includeSubdirectories) {
		this(root, DEFAULT_PERIOD, includeSubdirectories, null);
	}

	/**
	 * Constructs a new file monitor.
	 * 
	 * @param root
	 *            the file or directory to monitor.
	 * @param period
	 *            the check period (in milliseconds).
	 * @param includeSubdirectories
	 *            indicates whether subdirectories have to be also monitored or not.
	 */
	public FileMonitor(File root, long period, boolean includeSubdirectories) {
		this(root, period, includeSubdirectories, null);
	}

	/**
	 * Constructs a new file monitor.
	 * 
	 * @param root
	 *            the file or directory to monitor.
	 * @param period
	 *            the check period (in milliseconds). The absolute value is taken.
	 * @param includeSubdirectories
	 *            indicates whether subdirectories have to be also monitored or not.
	 * @param filter
	 *            a file filter used to restrict the files that have to be monitored.
	 * @throws IllegalArgumentException
	 *             if <code>root</code> is <code>null</code>.
	 */
	public FileMonitor(File root, long period, boolean includeSubdirectories, FileFilter filter) {
		if (root == null) {
			throw new IllegalArgumentException("Root is null.");
		}
		this.root = root;
		this.period = Math.abs(period);
		this.includeSubdirectories = includeSubdirectories;
		this.filter = filter;
	}

	/**
	 * Adds a file listener to the list. If <code>null</code>, this method does nothing.
	 * 
	 * @param l
	 *            the listener to add.
	 */
	public void addFileListener(FileListener l) {
		listenerList.add(FileListener.class, l);
	}

	/**
	 * Removes the file listener from the list.
	 * 
	 * @param l
	 *            the listener to remove.
	 */
	public void removeFileListener(FileListener l) {
		listenerList.remove(FileListener.class, l);
	}

	/**
	 * Fires the specified event to all registered listeners.
	 * 
	 * @param event
	 *            the event to fire.
	 */
	private void notifyListeners(FileEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == FileListener.class) {
				((FileListener) listeners[i + 1]).processFileEvent(event);
			}
		}
	}

	/**
	 * Recursively reads the underlying file system. For the first call forces recurse to true to include at least the first level of files
	 * contained in a directory.
	 */
	private void load(File file, Hashtable<File, FileInfo> v, boolean recurse) {
		if (file != null) {
			FileInfo fi = getFileInfo(file);
			v.put(fi._source, fi);
			if (file.isDirectory() && recurse) {
				File[] files = file.listFiles(filter);
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						load(files[i], v, includeSubdirectories);
					}
				}
			}
		}
	}

	/**
	 * Creates an object that stores useful monitoring information about a file.
	 * 
	 * @param file
	 *            the file.
	 * @return an object that stores useful monitoring information about a file.
	 */
	private FileInfo getFileInfo(File file) {
		if (file.exists()) {
			return new FileInfo(file, true, file.lastModified());
		} else {
			return new FileInfo(file, false, -1);
		}
	}

	/**
	 * Starts the file monitor.
	 */
	public synchronized void start() {
		started = true;
		if (thread == null) {
			thread = new Thread(this, "File monitor [" + root + "]");
			thread.start();
		}
	}

	/**
	 * Stops the file monitor. This method blocks until the monitoring thread dies.
	 */
	public synchronized void stop() {
		started = false;
		if ((thread != null) && (thread.isAlive())) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			thread = null;
		}
	}

	private void check(Hashtable<File, FileInfo> reference, Hashtable<File, FileInfo> newReference) {
		for (Iterator<File> it = reference.keySet().iterator(); it.hasNext();) {
			File refFile = (File) it.next();
			if (!newReference.containsKey(refFile)) {
				// Deleted
				// IMPORTANT ! When a file does not longer exist isDirectory() will return false.
				// There is no way to know whether the deleted element was a directory or a file.
				notifyListeners(new FileEvent(this, FileEvent.FILE_DELETE, refFile));
			} else {
				// The reference still exists, then check for modifications.
				FileInfo ref = (FileInfo) reference.get(refFile);
				FileInfo newRef = (FileInfo) newReference.get(refFile);
				if (!ref.equals(newRef)) {
					// The file has changed, so notify.
					notifyListeners(new FileEvent(this, FileEvent.FILE_MODIFY, refFile));
				}
			}
		}
		// Check for new files.
		for (Iterator<File> it = newReference.keySet().iterator(); it.hasNext();) {
			File file = (File) it.next();
			if (!reference.containsKey(file)) {
				// The file is new, notify.
				notifyListeners(new FileEvent(this, FileEvent.FILE_NEW, file));
			}
		}
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (started) {
			if (!initialLoad) {
				// long t0 = System.currentTimeMillis();
				load(root, _vectorFileInfo, true);
				// long t1 = System.currentTimeMillis();
				// SystemLog.debug(this, "run", "Initial load took " + (t1 - t0) + " ms.", null);
				initialLoad = true;
			} else {
				Hashtable<File, FileInfo> newFileInfo = new Hashtable<File, FileInfo>();
				// long t0 = System.currentTimeMillis();
				load(root, newFileInfo, true);
				// long t1 = System.currentTimeMillis();
				// SystemLog.debug(this, "run", "Load took " + (t1 - t0) + " ms.", null);
				check(_vectorFileInfo, newFileInfo);
				// long t2 = System.currentTimeMillis();
				// SystemLog.debug(this, "run", "Check took " + (t2 - t1) + " ms.", null);
				_vectorFileInfo = newFileInfo;
			}
			// Load / Check
			try {
				// SystemLog.debug(this, "run", "Sleeping " + period + " ms.", null);
				Thread.sleep(period);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("FileMonitor[");
		sb.append("root=").append(root).append(", ");
		sb.append("period=").append(period).append(", ");
		sb.append("includeSubdirectories=").append(includeSubdirectories).append("]");

		return sb.toString();
	}

	private class FileInfo {

		private File _source = null;

		private boolean _exists = false;

		private long _lastModified = -1;

		public FileInfo(File source, boolean exists, long lastModified) {
			_source = source;
			_exists = exists;
			_lastModified = lastModified;
		}

		/*
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj instanceof FileInfo) {
				FileInfo fi = (FileInfo) obj;
				return (fi._source.equals(_source) && (fi._exists == _exists) && (fi._lastModified == _lastModified));
			}
			return false;
		}

	}

}

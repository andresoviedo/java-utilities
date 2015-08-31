package org.andresoviedo.util.file.monitor;

import java.util.EventListener;

/**
 * The interface an object interested in file monitor events has to implement.
 * 
 */
public interface FileListener extends EventListener {

	/**
	 * Processes the file event.
	 * 
	 * @param e
	 *            the fired event.
	 */
	public void processFileEvent(FileEvent e);

}
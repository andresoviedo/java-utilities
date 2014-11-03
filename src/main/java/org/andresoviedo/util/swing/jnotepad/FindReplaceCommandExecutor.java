package org.andresoviedo.util.swing.jnotepad;

/**
 * The interface an object able to find and replace text has to implement.
 * 

 */
interface FindReplaceCommandExecutor {

	public boolean find();

	public void replace();

	public int replaceAll();

	public boolean replaceFind();

	// public void scopeChanged();

}

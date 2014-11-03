package org.andresoviedo.util.swing.jnotepad;

interface FindReplaceOptions {

	public static final int DIRECTION_FORWARD = 0;
	public static final int DIRECTION_BACKWARD = 1;

	public static final int SCOPE_ALL = 0;
	public static final int SCOPE_SELECTED_LINES = 1;

	public String getText();

	public String getReplaceText();

	public int getDirection();

	public int getScope();

	public void setScope(int scope);

	public boolean isCaseSensitive();

	public boolean isMatchWholeWord();

	public boolean isUseRegex();

	public void disableReplace();

}

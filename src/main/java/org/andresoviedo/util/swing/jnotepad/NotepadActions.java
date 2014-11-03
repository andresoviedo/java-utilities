package org.andresoviedo.util.swing.jnotepad;

public interface NotepadActions {

	public static final short ACTION_NEW = 0x0001;
	public static final short ACTION_OPEN = 0x0002;
	public static final short ACTION_SAVE = 0x0004;
	public static final short ACTION_SAVE_AS = 0x0008;
	public static final short ACTION_CUT = 0x0010;
	public static final short ACTION_COPY = 0x0020;
	public static final short ACTION_PASTE = 0x0040;
	public static final short ACTION_UNDO = 0x0080;
	public static final short ACTION_REDO = 0x0100;
	public static final short ACTION_FIND_REPLACE = 0x0200;
	public static final short ACTION_GOTO_LINE = 0x0400;

}

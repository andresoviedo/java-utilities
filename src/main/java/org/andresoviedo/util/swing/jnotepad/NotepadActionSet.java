package org.andresoviedo.util.swing.jnotepad;

public class NotepadActionSet implements NotepadActions {

	private short flags = 0x07FF;

	public NotepadActionSet() {
	}

	public boolean isVisible(short action) {
		return (flags & action) == action;
	}

	public void setVisible(short action, boolean visible) {
		this.flags = (visible) ? (short) (flags | action) : (short) (flags ^ action);
	}

	public void setAllVisible(boolean visible) {
		this.flags = (visible) ? (short) 0x07FF : (short) 0x0000;
	}

}

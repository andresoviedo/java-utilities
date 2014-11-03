package org.andresoviedo.util.swing.jdesktop;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * FramePositioningAction.
 * 

 */
class FramePositioningAction extends AbstractAction {

	static final int CASCADE_FRAMES = 0;

	static final int ICONIFY_FRAMES = 1;

	static final int RESTORE_FRAMES = 2;

	static final int TILE_FRAMES_HORIZONTALLY = 3;

	static final int TILE_FRAMES_VERTICALLY = 4;

	private BasicDesktopPane desktop;

	private int layer;

	private int actionType;

	/**
	 * Constructs a new frame positioning action.
	 * 
	 * @param desktop
	 *          the desktop pane.
	 * @param actionType
	 *          the desired action type.
	 */
	public FramePositioningAction(BasicDesktopPane desktop, int actionType) {
		this(desktop, -1, actionType);
	}

	/**
	 * Constructs a new frame positioning action.
	 * 
	 * @param desktop
	 *          the desktopPane.
	 * @param layer
	 *          the layer where the action has to be applied.
	 * @param actionType
	 *          the desired action type.
	 */
	public FramePositioningAction(BasicDesktopPane desktop, int layer, int actionType) {
		this.desktop = desktop;
		this.layer = layer;
		this.actionType = actionType;
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		switch (actionType) {
		case CASCADE_FRAMES:
			desktop.getFramePositioning().cascadeFrames(layer);
			break;
		case ICONIFY_FRAMES:
			desktop.getFramePositioning().iconifyFrames(layer, true);
			break;
		case RESTORE_FRAMES:
			desktop.getFramePositioning().iconifyFrames(layer, false);
			break;
		case TILE_FRAMES_HORIZONTALLY:
			desktop.getFramePositioning().tileFramesHorizontally(layer);
			break;
		case TILE_FRAMES_VERTICALLY:
			desktop.getFramePositioning().tileFramesVertically(layer);
			break;
		}
	}

}
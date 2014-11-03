package org.andresoviedo.util.swing.jdesktop;

import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

/**
 * Frame positioning.
 * 

 */
class FramePositioning {

	/**
	 * The default X offset for the first internal frame in cascade mode, relative to the desktop pane.
	 */
	public static final int X_OFFSET = 30;

	/**
	 * The default Y offset for the first internal frame in cascade mode, relative to the desktop pane.
	 */
	public static final int Y_OFFSET = 30;

	/**
	 * The associated desktop pane.
	 */
	private BasicDesktopPane desktop;

	/**
	 * Constructs a new frame positioning object.
	 * 
	 * @param desktop
	 *          the associated desktop pane.
	 */
	public FramePositioning(BasicDesktopPane desktop) {
		this.desktop = desktop;
	}

	/**
	 * Iconifies or restores all frames in the specified layer.
	 * 
	 * @param layer
	 *          the layer.
	 * @param iconify
	 *          <code>true</code> iconifies all frames, <code>false</code> restores them.
	 */
	public void iconifyFrames(int layer, boolean iconify) {
		JInternalFrame[] frames = desktop.getAllFramesInLayer(layer);

		JInternalFrame f;
		for (int i = 0; i < frames.length; i++) {
			f = frames[i];
			if (f.isIconifiable() && f.isVisible()) {
				try {
					f.setIcon(iconify);
				} catch (PropertyVetoException e) {
				}
			}
		}
	}

	/**
	 * Cascades all frames in the specified layer. Notice that frames are not resized, only their location are changed.
	 * 
	 * @param layer
	 *          the layer.
	 */
	public void cascadeFrames(int layer) {
		JInternalFrame[] frames = desktop.getAllFramesInLayer(layer);

		JInternalFrame f;
		int frameCounter = 0;
		for (int i = frames.length - 1; i >= 0; i--) {
			f = frames[i];
			if (!f.isIcon() && f.isVisible()) {
				f.setLocation(cascadeFrame(f, frameCounter++));
			}
		}
	}

	private Point cascadeFrame(JInternalFrame f, int count) {
		int w = f.getWidth();
		int h = f.getHeight();

		Rectangle viewP = new Rectangle(0, 0, desktop.getWidth(), desktop.getHeight());

		// get # of windows that fit horizontally
		int numFramesWide = (viewP.width - w) / X_OFFSET;
		if (numFramesWide < 1)
			numFramesWide = 1;

		// get # of windows that fit vertically
		int numFramesHigh = (viewP.height - h) / Y_OFFSET;
		if (numFramesHigh < 1)
			numFramesHigh = 1;

		// position relative to the current viewport (viewP.x/viewP.y)
		// (so new windows appear onscreen)
		int xLoc = viewP.x + X_OFFSET * ((count + 1) - (numFramesWide - 1) * (int) (count / numFramesWide));
		int yLoc = viewP.y + Y_OFFSET * ((count + 1) - numFramesHigh * (int) (count / numFramesHigh));

		return new Point(xLoc, yLoc);
	}

	/**
	 * Tiles all frames in the specified layer horizontally.
	 * 
	 * @param layer
	 *          the layer.
	 */
	public void tileFramesHorizontally(int layer) {
		tileFrames(layer, true);
	}

	/**
	 * Tiles all frames in the specified layer vertically.
	 * 
	 * @param layer
	 *          the layer.
	 */
	public void tileFramesVertically(int layer) {
		tileFrames(layer, false);
	}

	private void tileFrames(int layer, boolean horizontally) {
		int totalNonIconFrames = 0;
		Rectangle viewP = new Rectangle(0, 0, desktop.getWidth(), desktop.getHeight());
		JInternalFrame[] frames = desktop.getAllFramesInLayer(layer);

		for (int i = 0; i < frames.length; i++) {
			if (!frames[i].isIcon() && frames[i].isVisible()) {
				totalNonIconFrames++;
			}
		}

		if (totalNonIconFrames > 0) {
			int frameWidth = horizontally ? viewP.width : (viewP.width / totalNonIconFrames);
			int frameHeight = horizontally ? (viewP.height / totalNonIconFrames) : viewP.height;

			int i = 0;
			for (int curCol = 0; curCol < totalNonIconFrames; curCol++) {
				while (frames[i].isIcon() || !frames[i].isVisible()) {
					// Find the next visible frame.
					i++;
				}
				frames[i].setBounds(horizontally ? 0 : (curCol * frameWidth), horizontally ? (curCol * frameHeight) : 0, frameWidth, frameHeight);
				i++;
			}
		}
	}

}
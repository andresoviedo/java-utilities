package org.andresoviedo.util.swing.jdesktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 * A desktop pane with an scroll pane as the parent (optional), a button pane containing a toggle button for each internal frame (optional)
 * and the ability to position all frames (tile, cascade, etc.).
 * 
 */
public class BasicDesktopPane extends JDesktopPane {

	/**
	 * Hashtable storing the selected frame in each layer.
	 */
	private Map<Integer, JInternalFrame> hshSelectedFrames = new Hashtable<Integer, JInternalFrame>();

	/**
	 * Optional scroll pane holding this desktop pane.
	 */
	private BasicDesktopScrollPane scrollPane;

	/**
	 * A panel holding visible internal frame buttons to show them.
	 */
	private JPanel pnlButtons;

	/**
	 * Button group for internal frame buttons.
	 */
	private ButtonGroup buttonGroup;

	/**
	 * Listener to internal frame events.
	 */
	private BasicDesktopListener listener;

	/**
	 * Frame positioning.
	 */
	private FramePositioning positioning;

	/**
	 * Background image.
	 */
	private ImageIcon backgroundImage;

	/**
	 * Constructs a new basic desktop pane.
	 */
	public BasicDesktopPane() {
		setDragMode(OUTLINE_DRAG_MODE);
		setDesktopManager(new BasicDesktopManager(this));

		positioning = new FramePositioning(this);
	}

	/**
	 * Returns the associated frame positioning object.
	 * 
	 * @return the associated frame positioning object.
	 */
	FramePositioning getFramePositioning() {
		return this.positioning;
	}

	/*
	 * @see javax.swing.JLayeredPane#addImpl(java.awt.Component, java.lang.Object, int)
	 */
	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);

		if (comp instanceof JInternalFrame) {
			JInternalFrame f = (JInternalFrame) comp;

			f.addComponentListener(getDesktopListener());
			f.addInternalFrameListener(getDesktopListener());
		}
	}

	/*
	 * @see java.awt.Container#remove(int)
	 */
	public void remove(int index) {
		Component c = getComponent(index);
		super.remove(index);
		if (c instanceof JInternalFrame) {
			JInternalFrame f = (JInternalFrame) c;
			f.removeComponentListener(getDesktopListener());
			f.removeInternalFrameListener(getDesktopListener());
		}
	}

	/**
	 * Gets the desktop listener instance.
	 * 
	 * @return the desktop listener instance.
	 */
	protected BasicDesktopListener getDesktopListener() {
		if (listener == null) {
			listener = new BasicDesktopListener(this);
		}
		return listener;
	}

	/**
	 * Returns desktop's scroll pane.
	 * 
	 * @return desktop's scroll pane.
	 */
	public BasicDesktopScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new BasicDesktopScrollPane(this);
		}
		return scrollPane;
	}

	/**
	 * Returns the panel holding visible internal frame buttons.
	 * 
	 * @return the panel holding visible internal frame buttons.
	 */
	public JPanel getButtonPanel() {
		if (pnlButtons == null) {
			buttonGroup = new ButtonGroup();

			pnlButtons = new JPanel(new GridLayout(1, 0, 0, 0));
			pnlButtons.setMaximumSize(new Dimension(100, 20));

			BasicInternalFrame f = null;

			JInternalFrame[] frames = getAllFrames();
			for (int i = 0; i < frames.length; i++) {
				if (frames[i] instanceof BasicInternalFrame) {
					f = (BasicInternalFrame) frames[i];
					if (f.isVisible() || f.isIcon()) {
						addButton(f);
					}
				}
			}
		}
		return pnlButtons;
	}

	/**
	 * Adds the internal frame's button to the panel.
	 * 
	 * @param f
	 *            the internal frame which button has to be added.
	 */
	void addButton(BasicInternalFrame f) {
		if ((f != null) && f.getUseDesktopToggleButton() && (pnlButtons != null)) {
			buttonGroup.add(f.getDesktopToggleButton());

			pnlButtons.add(f.getDesktopToggleButton());
			f.getDesktopToggleButton().setSelected(true);
			pnlButtons.revalidate();
			pnlButtons.repaint();
		}
	}

	/**
	 * Removes the internal frame's button from the panel.
	 * 
	 * @param f
	 *            the internal frame which button has to be removed.
	 */
	void removeButton(BasicInternalFrame f) {
		if (f != null && pnlButtons != null) {
			buttonGroup.remove(f.getDesktopToggleButton());

			pnlButtons.remove(f.getDesktopToggleButton());
			pnlButtons.revalidate();
			pnlButtons.repaint();
		}
	}

	/**
	 * Gets the selected frame in the specified layer.
	 * 
	 * @param layer
	 *            the layer in which the selected frame has to be retrieved.
	 */
	protected JInternalFrame getSelectedFrameInLayer(int layer) {
		return hshSelectedFrames.get(layer);
	}

	/**
	 * Sets the frame as the selected frame in its layer.
	 * 
	 * @param f
	 *            the internal frame.
	 */
	protected void setSelectedFrameInLayer(JInternalFrame f) {
		if (f == null) {
			return;
		}
		hshSelectedFrames.put(f.getLayer(), f);
	}

	/**
	 * Removes the selected frame in the specified layer from the hashtable.
	 * 
	 * @param layer
	 *            the layer in which the selected frame has to be retrieved.
	 */
	protected void removeSelectedFrameInLayer(int layer) {
		hshSelectedFrames.remove(layer);
	}

	/**
	 * Activate the next frame in the specified layer. This method is invoked when a frame is closed or iconified.
	 * 
	 * @param layer
	 *            the layer where the next frame has to be activated.
	 */
	public void activateNextFrameInLayer(int layer) {
		JInternalFrame f = getNextFrameInLayer(layer);
		if (f != null) {
			try {
				f.setSelected(true);
			} catch (PropertyVetoException e) {
			}
		}
	}

	/**
	 * Gets the internal frame that should be selected (the first visible one that is not selected nor iconified).
	 * 
	 * @param layer
	 *            the layer where to look for the next frame.
	 * @return the internal frame that should be selected.
	 */
	public JInternalFrame getNextFrameInLayer(int layer) {
		// getAllFramesInLayer() returns visible frames and iconified as well.
		JInternalFrame[] frames = getAllFramesInLayer(layer);
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].isVisible() && !frames[i].isIcon()) {
				return frames[i];
			}
		}
		return null;
	}

	/**
	 * Centers the frame in the desktop pane.
	 * 
	 * @param f
	 *            the frame to center.
	 */
	public void centerFrame(JInternalFrame f) {
		if (f.getParent() != this) {
			return;
		}

		Rectangle r;
		if (getScrollPane() != null) {
			r = getScrollPane().getViewport().getViewRect();
		} else {
			r = getBounds();
		}

		Dimension d1 = r.getSize();
		Dimension d2 = f.getSize();
		f.setLocation((d1.width - d2.width) / 2, (d1.height - d2.height) / 2);
	}

	/**
	 * Sets the background image.
	 * 
	 * @param backgroundImage
	 *            the bakcground image to set.
	 */
	public void setBackgroundImage(ImageIcon backgroundImage) {
		this.backgroundImage = backgroundImage;
		this.repaint();
	}

	/**
	 * Overwritten to paint the background image, if set.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Paint the background image.
		if (backgroundImage != null) {
			int w = backgroundImage.getIconWidth();
			int h = backgroundImage.getIconHeight();

			boolean scale = (w > getWidth() && h > getHeight());

			if (scale) {
				g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), 0, 0, w, h, this);
			} else {
				// Center the image and draw a black rectangle around it.
				int x = (getWidth() - w) / 2;
				int y = (getHeight() - h) / 2;
				g.drawImage(backgroundImage.getImage(), x, y, x + w, y + h, 0, 0, w, h, this);
				g.setColor(Color.black);
				g.drawRect(x, y, w, h);
			}
		}
	}

	/**
	 * Returns an action that cascades internal frames.
	 * 
	 * @param layer
	 *            the layer where frames have to be arranged.
	 * @return an action that cascades internal frames.
	 */
	public Action createCascadeAction(int layer) {
		return new FramePositioningAction(this, layer, FramePositioningAction.CASCADE_FRAMES);
	}

	/**
	 * Returns an action that minimizes internal frames.
	 * 
	 * @param layer
	 *            the layer where frames have to be minimized.
	 * @return an action that minimizes all internal frames.
	 */
	public Action createIconifyAction(int layer) {
		return new FramePositioningAction(this, layer, FramePositioningAction.ICONIFY_FRAMES);
	}

	/**
	 * Returns an action that restores internal frames.
	 * 
	 * @param layer
	 *            the layer where frames have to be minimized.
	 * @return an action that restores all internal frames.
	 */
	public Action createRestoreAction(int layer) {
		return new FramePositioningAction(this, layer, FramePositioningAction.RESTORE_FRAMES);
	}

	/**
	 * Returns an action that tiles internal frames.
	 * 
	 * @param layer
	 *            the layer where frames have to be minimized.
	 * @param horizontally
	 *            <code>true</code> if frames have to be tiled horizontally.
	 * @return an action that tiles internal frames.
	 */
	public Action createTileAction(int layer, boolean horizontally) {
		return new FramePositioningAction(this, layer, horizontally ? FramePositioningAction.TILE_FRAMES_HORIZONTALLY
				: FramePositioningAction.TILE_FRAMES_VERTICALLY);
	}

}
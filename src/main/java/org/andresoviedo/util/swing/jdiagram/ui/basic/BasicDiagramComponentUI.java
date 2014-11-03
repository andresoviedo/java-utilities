package org.andresoviedo.util.swing.jdiagram.ui.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.andresoviedo.util.swing.jdiagram.DiagramComponent;
import org.andresoviedo.util.swing.jdiagram.ui.DiagramComponentUI;

public class BasicDiagramComponentUI extends DiagramComponentUI implements FocusListener, MouseListener, ChangeListener {

	/**
	 * A static instance of the component UI - shared between all DiagramComponent instances.
	 */
	private static BasicDiagramComponentUI componentUI = new BasicDiagramComponentUI();

	/**
	 * The preferred size for the component, until computed.
	 */
	private static final Dimension PREFERRED_SIZE = new Dimension(100, 60);

	public static ComponentUI createUI(JComponent c) {
		return componentUI;
	}

	/*
	 * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
	 */
	public void installUI(JComponent c) {
		installDefaults((DiagramComponent) c);
		installListeners((DiagramComponent) c);
		installKeyboardActions((DiagramComponent) c);
	}

	/*
	 * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
	 */
	public void uninstallUI(JComponent c) {
		uninstallDefauls((DiagramComponent) c);
		uninstallListeners((DiagramComponent) c);
		uninstallKeyboardActions((DiagramComponent) c);
	}

	/*
	 * @see javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
	 */
	public Dimension getPreferredSize(JComponent c) {
		return PREFERRED_SIZE;
	}

	/**
	 * Initialize diagram component properties.
	 * 
	 * @param c
	 *          the diagram component.
	 */
	protected void installDefaults(DiagramComponent c) {
		LookAndFeel.installBorder(c, "DiagramComponent.border");
		LookAndFeel.installColorsAndFont(c, "DiagramComponent.background", "DiagramComponent.foreground", "DiagramComponent.font");

		Color color = c.getHeaderBackground();
		if ((color == null) || (color instanceof UIResource)) {
			c.setHeaderBackground(UIManager.getColor("DiagramComponent.headerBackground"));
		}

		color = c.getHeaderForeground();
		if ((color == null) || (color instanceof UIResource)) {
			c.setHeaderForeground(UIManager.getColor("DiagramComponent.headerForeground"));
		}

		color = c.getHeaderSelectionBackground();
		if ((color == null) || (color instanceof UIResource)) {
			c.setHeaderSelectionBackground(UIManager.getColor("DiagramComponent.headerSelectionBackground"));
		}

		color = c.getHeaderSelectionForeground();
		if ((color == null) || (color instanceof UIResource)) {
			c.setHeaderSelectionForeground(UIManager.getColor("DiagramComponent.headerSelectionForeground"));
		}
	}

	/**
	 * Set the DiagramComponent properties that haven't been explicitly overridden to <code>null</code>. A property is considered overridden
	 * if its current value is not a UIResource.
	 * 
	 * @param c
	 *          the diagram component.
	 */
	protected void uninstallDefauls(DiagramComponent c) {
		LookAndFeel.uninstallBorder(c);
		if (c.getHeaderBackground() instanceof UIResource) {
			c.setHeaderBackground(null);
		}
		if (c.getHeaderForeground() instanceof UIResource) {
			c.setHeaderForeground(null);
		}
		if (c.getHeaderSelectionBackground() instanceof UIResource) {
			c.setHeaderSelectionBackground(null);
		}
		if (c.getHeaderSelectionForeground() instanceof UIResource) {
			c.setHeaderSelectionForeground(null);
		}
	}

	protected void installListeners(DiagramComponent c) {
		c.addFocusListener(this);
		c.addMouseListener(this);
		c.addChangeListener(this);
	}

	protected void uninstallListeners(DiagramComponent c) {
		c.removeFocusListener(this);
		c.removeMouseListener(this);
		c.removeChangeListener(this);
	}

	protected void installKeyboardActions(DiagramComponent c) {
		ActionMap map = new ActionMapUIResource();
		map.put("select", new SelectAction(c));

		SwingUtilities.replaceUIActionMap(c, map);

		InputMap im = (InputMap) UIManager.get("DiagramComponent.focusInputMap");

		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, im);
	}

	protected void uninstallKeyboardActions(DiagramComponent c) {
		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, null);
		SwingUtilities.replaceUIActionMap(c, null);
	}

	/*
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics, javax.swing.JComponent)
	 */
	public void paint(Graphics g, JComponent c) {
		// Background is automatically painted (depending on component's opacity) in ComponentUI.update()).
		DiagramComponent dc = (DiagramComponent) c;

		paintHeader(g, dc);
		paintIcon(g, dc);

		if (dc.isFocusPainted() && c.isFocusOwner()) {
			paintFocus(g, dc);
		}
	}

	/**
	 * Paints the header of the diagram component.
	 * 
	 * @param g
	 *          the graphics context.
	 * @param c
	 *          the diagram component.
	 */
	protected void paintHeader(Graphics g, DiagramComponent c) {
		Dimension dim = c.getSize();

		// Draw header's background.
		Color color = c.isSelected() ? c.getHeaderSelectionBackground().darker() : (c.isHighlighted() ? c.getHeaderSelectionBackground() : c
				.getHeaderBackground());

		if (!c.isEnabled()) {
			color = Color.lightGray;
		}

		// TODO: add a property, preferredHeaderSize.
		Rectangle r = computeHeaderBounds(c);

		g.setColor(color);
		g.fillRect(r.x, r.y, r.width, r.height);

		// Draw the string.
		String s = c.getHeaderText();
		if (s == null) {
			return;
		}

		FontMetrics fm = g.getFontMetrics();

		s = getClippedText(fm, s, dim.width - 4);

		int w = fm.stringWidth(s);
		int h = fm.getAscent() + fm.getDescent();
		int x = Math.max(2, (dim.width - w) / 2);
		int y = ((r.height - h) / 2) + fm.getAscent();

		g.setColor((c.isSelected() || c.isHighlighted()) ? c.getHeaderSelectionForeground() : c.getHeaderForeground());
		g.drawString(s, x, y);
	}

	/**
	 * Paints the icon of the diagram component.
	 * 
	 * @param g
	 *          the graphics context.
	 * @param c
	 *          the diagram component.
	 */
	protected void paintIcon(Graphics g, DiagramComponent c) {
		Icon icon = c.isEnabled() ? c.getIcon() : c.getDisabledIcon();
		if (icon != null) {
			Dimension dim = c.getSize();

			int x = (dim.width - icon.getIconWidth()) / 2;
			int y = (dim.height - 20 - icon.getIconHeight()) / 2 + 20;

			icon.paintIcon(c, g, x, y);
		}
	}

	/**
	 * Paints the focus of the diagram component.
	 * 
	 * @param g
	 *          the graphics component.
	 */
	protected void paintFocus(Graphics g, DiagramComponent c) {
		Rectangle r = computeContentBounds(c);

		Insets insets = c.getInsets();
		r.x += insets.left;
		r.width -= (insets.left + insets.right);
		r.height -= insets.bottom;

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
	}

	protected Rectangle computeHeaderBounds(DiagramComponent c) {
		return new Rectangle(0, 0, c.getWidth(), 20);
	}

	protected Rectangle computeContentBounds(DiagramComponent c) {
		return new Rectangle(0, 20, c.getWidth(), c.getHeight() - 20);
	}

	/**
	 * Computes a clipped version of the specified text depending on the font metrics and the available width.
	 * 
	 * @param fm
	 *          the font metrics.
	 * @param text
	 *          the text.
	 * @param availableWidth
	 *          the available width.
	 * @return a clipped version of the text.
	 */
	private String getClippedText(FontMetrics fm, String text, int availableWidth) {
		if ((text == null) || (text.length() == 0)) {
			return "";
		}

		int textWidth = fm.stringWidth(text);
		if (textWidth < availableWidth) {
			return text;
		}

		String clippedText = "...";
		int totalWidth = fm.stringWidth(clippedText);
		int nChars;
		for (nChars = 0; nChars < text.length(); nChars++) {
			totalWidth += fm.charWidth(text.charAt(nChars));
			if (totalWidth > availableWidth) {
				break;
			}
		}
		return text.substring(0, nChars) + clippedText;
	}

	/*
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		((JComponent) e.getSource()).repaint();
	}

	/*
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		((Component) e.getSource()).repaint();
	}

	/*
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			DiagramComponent c = (DiagramComponent) e.getSource();
			if (c.isEnabled()) {
				c.getModel().setSelected(!c.getModel().isSelected());
			}
		}
	}

	/*
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		if (c.isEnabled() && !c.isFocusOwner() && c.isRequestFocusEnabled()) {
			c.requestFocus();
		}
	}

	/*
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/*
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		((JComponent) e.getSource()).repaint();
	}

	static class SelectAction extends AbstractAction {
		DiagramComponent c = null;

		SelectAction(DiagramComponent c) {
			this.c = c;
		}

		public void actionPerformed(ActionEvent e) {
			c.setSelected(!c.isSelected());
			if (!c.hasFocus()) {
				c.requestFocus();
			}
		}

		public boolean isEnabled() {
			return c.getModel().isEnabled();
		}
	}

}
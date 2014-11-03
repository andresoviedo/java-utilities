package org.andresoviedo.util.swing.jdiagram;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class DiagramComponentGroup {

	private String headerText;

	private Insets insets;

	private boolean singleSelection = false;

	private DiagramComponentModel selection = null;

	private List<Component> components = new Vector<Component>();

	public DiagramComponentGroup() {
		this(false);
	}

	public DiagramComponentGroup(boolean singleSelection) {
		this.singleSelection = singleSelection;
		this.insets = new Insets(10, 10, 10, 10);
	}

	public void addComponent(DiagramComponent component) {
		if (component == null) {
			return;
		}

		components.add(component);

		if (singleSelection) {
			if (component.isSelected()) {
				if (selection == null) {
					selection = component.getModel();
				} else {
					component.setSelected(false);
				}
			}
			component.getModel().setGroup(this);
		}
	}

	public void removeComponent(DiagramComponent component) {
		if (component == null) {
			return;
		}

		components.remove(component);
		if (component.getModel() == selection) {
			selection = null;
		}
		component.getModel().setGroup(null);
	}

	public void removeAllComponents() {
		components.clear();
		selection = null;
	}

	public List<Component> getComponents() {
		return components;
	}

	public int getComponentCount() {
		return components.size();
	}

	public boolean isSelected(DiagramComponentModel m) {
		return (m == selection);
	}

	public void setSelected(DiagramComponentModel m, boolean selected) {
		if (singleSelection) {
			if (selected && (m != null) && (m != selection)) {
				DiagramComponentModel oldSelection = selection;
				selection = m;
				if (oldSelection != null) {
					oldSelection.setSelected(false);
				}
				m.setSelected(true);
			}
		}
	}

	public void clearSelection() {
		if (singleSelection) {
			DiagramComponentModel oldSelection = selection;
			selection = null;
			if (oldSelection != null) {
				oldSelection.setSelected(false);
			}
		}
	}

	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public Rectangle getBounds() {
		Rectangle r = null;

		for (Iterator<Component> it = components.iterator(); it.hasNext();) {
			if (r == null) {
				r = new Rectangle(it.next().getBounds());
			} else {
				r = r.union(it.next().getBounds());
			}
		}

		return r;
	}

	public void layout(int startX, int startY, int hGap, int vGap) {
		Point point = null;
		Component component = null;
		for (Iterator<Component> it = components.iterator(); it.hasNext();) {
			component = it.next();

			point = component.getLocation();
			point.x = startX;
			point.y = startY;

			component.setLocation(point);

			startX += hGap;
			startY += vGap;
		}
	}

	public void translate(int dx, int dy) {
		Point point = null;
		Component component = null;
		for (Iterator<Component> it = components.iterator(); it.hasNext();) {
			component = it.next();

			point = component.getLocation();
			point.x += dx;
			point.y += dy;

			component.setLocation(point);
		}
	}

	public void draw(Graphics g) {
		Rectangle r = getBounds();
		if (r == null) {
			return;
		}

		r.x -= insets.left;
		r.y -= insets.top;
		r.width += insets.left + insets.right;
		r.height += insets.top + insets.bottom;

		g.setColor(Color.white);
		g.fillRect(r.x, r.y, r.width, r.height);
		g.setColor(Color.red);
		g.drawRect(r.x, r.y, r.width, r.height);

		// Draw header text.
		r.y -= 20;
		r.height = 20;

		g.setColor(Color.red);
		g.fillRect(r.x, r.y, r.width, r.height);

		String s = getHeaderText();
		if (s == null) {
			return;
		}

		FontMetrics fm = g.getFontMetrics();

		s = getClippedText(fm, s, r.width);

		int w = fm.stringWidth(s);
		int h = fm.getAscent() + fm.getDescent();
		int x = Math.max(2, (r.width - w) / 2);
		int y = ((20 - h) / 2) + fm.getAscent();

		g.translate(r.x, r.y);
		g.setColor(Color.white);
		g.drawString(s, x, y);
		g.translate(-r.x, -r.y);
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

}
package org.andresoviedo.util.swing.jdiagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Link {

	private Component source;

	private Component destination;

	public Link(Component source, Component destination) {
		this.source = source;
		this.destination = destination;
	}

	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);

		int x1 = (int) source.getBounds().getCenterX();
		int y1 = (int) source.getBounds().getCenterY();
		int x2 = (int) destination.getBounds().getCenterX();
		int y2 = (int) destination.getBounds().getCenterY();

		int y = y1 + (y2 - y1) / 2;

		int[] xPoints = { x1, x1, x2, x2 };
		int[] yPoints = { y1, y, y, y2 };

		Stroke stroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2.0f));
		g2.drawPolyline(xPoints, yPoints, 4);
		g2.setStroke(stroke);
	}

}

package org.andresoviedo.util.swing.jmarquee;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * <code>JMarquee</code> is a label that animates the text. It displays continously the text from right to left.
 * 

 */
public class JMarquee extends JLabel implements Runnable {

	private int x, threshold;

	private int w;

	private String text;

	private Thread thread = null;

	public synchronized void start() {
		if (thread == null) {
			reset();

			thread = new Thread(this, "JMarquee");
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}

	public synchronized void stop() {
		if (thread != null) {
			thread.interrupt();
		}
		thread = null;
	}

	/*
	 * @see javax.swing.JLabel#setText(java.lang.String)
	 */
	public void setText(String text) {
		this.text = text;

		Graphics g = getGraphics();
		if (g != null && text != null) {
			w = SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
		} else {
			w = 0;
		}
	}

	/*
	 * @see javax.swing.JLabel#getText()
	 */
	public String getText() {
		return " ";
	}

	/*
	 * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
	 */
	public void setIcon(Icon icon) {
		super.setIcon(icon);

		threshold = getThreshold();
	}

	private int getThreshold() {
		int x = getInsets().left;
		if (getIcon() != null) {
			x += getIcon().getIconWidth();
			x += getIconTextGap();
		}
		return x;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Thread me = Thread.currentThread();

		while (thread == me && !isVisible() || getWidth() == 0) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}

		while (thread == me) {
			animate();
			repaint();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
			}
		}

		reset();
		repaint();
	}

	private void animate() {
		x--;

		if (w == 0) {
			Graphics g = getGraphics();
			if (g != null && text != null) {
				w = SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
			}
		}

		if (x + w < threshold) {
			x = getWidth();
		}
	}

	private void reset() {
		x = getWidth();
	}

	/*
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (text != null) {
			Rectangle r = g.getClipBounds();

			g.setColor(getForeground());
			g.setClip(threshold, 0, getWidth() - threshold, getHeight());
			g.drawString(text, x, g.getFontMetrics().getAscent() + getInsets().top);
			g.setClip(r);
		}
	}

}
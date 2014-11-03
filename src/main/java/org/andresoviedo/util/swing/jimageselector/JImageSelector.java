package org.andresoviedo.util.swing.jimageselector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jimageselector.resources.Resources;


/**
 * A component used to load an image and capture a portion of it.
 * 

 */
public class JImageSelector extends JPanel implements ActionListener, ChangeListener, ItemListener, MouseInputListener {

	public static final int OPACITY_MAX = 100;
	public static final int OPACITY_MIN = 0;

	public static final int SCALE_FACTOR_MAX = 400;
	public static final int SCALE_FACTOR_MIN = 1;

	private int areaX;
	private int areaY;
	private int areaWidth;
	private int areaHeight;
	private int targetX;
	private int targetY;

	private int imageWidth;
	private int imageHeight;

	private int scaleFactor = 100;
	private int opacity = 50;
	private int zoomLevel = 1;

	private Color opacityColor = Color.BLACK;

	private Image image;

	private JSlider sldScaling;
	private JSlider sldOpacity;
	private JComboBox cboZoomLevel;
	private JTextField tfScalingValue;
	private JTextField tfOpacityValue;
	private JScrollPane spImage;
	private JButton btnCancel;
	private JButton btnCapture;
	private JButton btnCenter;
	private JButton btnOpacityColor;
	private JButton btnRotateLeft;
	private JButton btnRotateRight;

	private ImagePanel pnlImage;

	private boolean showCancelButton = false;

	/**
	 * Constructs a new <code>JImageSelector</code> with no image.
	 */
	public JImageSelector() {
		this(null, 0, 0, 100, 100);
	}

	/**
	 * Constructs a new <code>JImageSelector</code> with the specified image.
	 * 
	 * @param image
	 *          an image.
	 */
	public JImageSelector(Image image) {
		this(image, 0, 0, 100, 100);
	}

	/**
	 * Constructs a new <code>JImageSelector</code>.
	 * 
	 * @param image
	 *          the image to display.
	 * @param initialX
	 *          the initial X of the selection area.
	 * @param initialY
	 *          the initial Y of the selection area.
	 * @param initialWidth
	 *          the initial width of the selection area.
	 * @param initialHeight
	 *          the initial height of the selection area.
	 */
	public JImageSelector(Image image, int initialX, int initialY, int initialWidth, int initialHeight) {
		this.image = image;
		if (image != null) {
			imageWidth = image.getWidth(null);
			imageHeight = image.getHeight(null);
		}

		areaX = initialX;
		areaY = initialY;
		areaWidth = initialWidth;
		areaHeight = initialHeight;
		targetX = initialX + initialWidth / 2;
		targetY = initialY + initialHeight / 2;

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridheight = 5;

		add(getImageComponent(), gbc);

		gbc.gridx = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;

		add(getScalingComponent(), gbc);

		gbc.gridy++;

		add(getOpacityComponent(), gbc);

		gbc.gridy++;

		add(getZoomComponent(), gbc);

		gbc.gridy++;

		add(getOperationsPanel(), gbc);

		gbc.gridy++;
		gbc.anchor = GridBagConstraints.SOUTH;

		add(getButtonComponent(), gbc);

		generateCurrentImage();
	}

	/**
	 * Adds an action listener to the 'Cancel' button.
	 * 
	 * @param l
	 *          the action listener to add.
	 */
	public void addCancelListener(ActionListener l) {
		btnCancel.addActionListener(l);
	}

	/**
	 * Adds an action listener to the 'Capture' button.
	 * 
	 * @param l
	 *          the action listener to add.
	 */
	public void addCaptureListener(ActionListener l) {
		btnCapture.addActionListener(l);
	}

	/**
	 * Returns the image currently being displayed.
	 * 
	 * @return the image currently being displayed.
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Returns the opacity.
	 * 
	 * @return the opacity.
	 */
	public int getOpacity() {
		return opacity;
	}

	/**
	 * Returns the opacity color.
	 * 
	 * @return the opacity color.
	 */
	public Color getOpacityColor() {
		return opacityColor;
	}

	/**
	 * Returns the scale factor.
	 * 
	 * @return the scale factor.
	 */
	public int getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * Returns the selected image.
	 * 
	 * @return the selected image.
	 */
	public Image getSelectedImage() {
		BufferedImage bi = new BufferedImage(areaWidth, areaHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2D = bi.createGraphics();
		g2D.setColor(Color.BLACK);
		g2D.fillRect(0, 0, areaWidth, areaHeight);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		int targetAreaX = areaX * 100 / scaleFactor;
		int targetAreaY = areaY * 100 / scaleFactor;
		int targetAreaWidth = areaWidth * 100 / scaleFactor;
		int targetAreaHeight = areaHeight * 100 / scaleFactor;

		if (image != null) {
			g2D.drawImage(image, 0, 0, bi.getWidth(), bi.getHeight(), targetAreaX, targetAreaY, targetAreaX + targetAreaWidth, targetAreaY
					+ targetAreaHeight, null);
		}
		g2D.dispose();

		return bi;
	}

	/**
	 * Returns the current zoom level.
	 * 
	 * @return the current zoom level.
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * Removes an action listener from the 'Cancel' button.
	 * 
	 * @param l
	 *          the action listener to add.
	 */
	public void removeCancelListener(ActionListener l) {
		btnCancel.removeActionListener(l);
	}

	/**
	 * Removes an action listener from the 'Capture' button.
	 * 
	 * @param l
	 *          the action listener to add.
	 */
	public void removeCaptureListener(ActionListener l) {
		btnCapture.removeActionListener(l);
	}

	/**
	 * Sets the image to display.
	 * 
	 * @param image
	 *          the image to display.
	 */
	public void setImage(Image image) {
		this.image = image;
		if (image != null) {
			imageWidth = image.getWidth(null);
			imageHeight = image.getHeight(null);
		}
		generateCurrentImage();
	}

	/**
	 * Sets the opacity.
	 * 
	 * @param opacity
	 *          the new opacity.
	 * @throws IllegalArgumentException
	 *           if <code>opacity</code> is out of range.
	 */
	public void setOpacity(int opacity) {
		if ((opacity < 0) || (opacity > 100)) {
			throw new IllegalArgumentException("Invalid opacity, should be from 0 to 100.");
		}
		if (this.opacity != opacity) {
			this.opacity = opacity;
			tfOpacityValue.setText(String.valueOf(opacity));
			sldOpacity.setValue(opacity);
			generateCurrentImage();
		}
	}

	/**
	 * Sets the opacity color to use. If the supplied color is null, this method does nothing.
	 * 
	 * @param opacityColor
	 *          the new color.
	 */
	public void setOpacityColor(Color opacityColor) {
		if ((opacityColor != null) && !opacityColor.equals(this.opacityColor)) {
			this.opacityColor = opacityColor;
			updateOpacityColorButton();
			generateCurrentImage();
		}
	}

	/**
	 * Sets the scale factor.
	 * 
	 * @param scaleFactor
	 *          the new scale factor.
	 * @throws IllegalArgumentException
	 *           if <code>scaleFactor</code> is out of range.
	 */
	public void setScaleFactor(int scaleFactor) {
		if ((scaleFactor < SCALE_FACTOR_MIN) || (scaleFactor > SCALE_FACTOR_MAX)) {
			throw new IllegalArgumentException("Invalid scale factor, should be from 1 to 400.");
		}
		if (this.scaleFactor != scaleFactor) {
			this.scaleFactor = scaleFactor;
			areaX = targetX * scaleFactor / 100 - areaWidth / 2;
			areaY = targetY * scaleFactor / 100 - areaHeight / 2;
			checkAreaBounds();

			tfScalingValue.setText(String.valueOf(scaleFactor));
			sldScaling.setValue(scaleFactor);
			generateCurrentImage();
			centerImage();
		}
	}

	/**
	 * Sets the zoom level.
	 * 
	 * @param zoomLevel
	 *          the new zoom level.
	 * @throws IllegalArgumentException
	 *           if <code>zoomLevel</code> is out of range.
	 */
	public void setZoomLevel(int zoomLevel) {
		if ((zoomLevel < 1) || (zoomLevel > 4)) {
			throw new IllegalArgumentException("Invalid zoom level, should be from 1 to 4.");
		}
		if (this.zoomLevel != zoomLevel) {
			this.zoomLevel = zoomLevel;
			generateCurrentImage();
			centerImage();
		}
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfScalingValue) {
			try {
				setScaleFactor(Integer.parseInt(tfScalingValue.getText()));
			} catch (Exception ex) {
				SwingUtils.showErrorDialog(this, Resources.getMessage(Resources.PATTERN_INVALID_SCALE_FACTOR, new Object[] {
						String.valueOf(SCALE_FACTOR_MIN), String.valueOf(SCALE_FACTOR_MAX) }));
			}
		} else if (e.getSource() == tfOpacityValue) {
			try {
				setOpacity(Integer.parseInt(tfOpacityValue.getText()));
			} catch (Exception ex) {
				SwingUtils.showErrorDialog(this, Resources.getMessage(Resources.PATTERN_INVALID_OPACITY, new Object[] {
						String.valueOf(OPACITY_MIN), String.valueOf(OPACITY_MAX) }));
			}
		} else if (e.getSource() == btnOpacityColor) {
			attemptSelectOpacityColor();
		} else if (e.getSource() == btnCenter) {
			centerImage();
		} else if (e.getSource() == btnRotateLeft) {
			rotateLeft();
		} else if (e.getSource() == btnRotateRight) {
			rotateRight();
		}
	}

	/*
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		setZoomLevel(((Integer) cboZoomLevel.getSelectedItem()).intValue());
	}

	/*
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		centerAreaOnPoint(e.getX() / zoomLevel, e.getY() / zoomLevel);
	}

	/*
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
	}

	/*
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		centerAreaOnPoint(e.getX() / zoomLevel, e.getY() / zoomLevel);
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
		centerAreaOnPoint(e.getX() / zoomLevel, e.getY() / zoomLevel);
	}

	/*
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Rotates the image to the left.
	 */
	public void rotateLeft() {
		if (image != null) {
			centerAreaOnPoint(targetY * scaleFactor / 100, (imageWidth - targetX) * scaleFactor / 100);
			rotateImage(-90);
			checkAreaBounds();
			centerImage();
		}
	}

	/**
	 * Rotates the image to the right (clockwise).
	 */
	public void rotateRight() {
		if (image != null) {
			centerAreaOnPoint((imageHeight - targetY) * scaleFactor / 100, targetX * scaleFactor / 100);
			rotateImage(90);
			checkAreaBounds();
			centerImage();
		}
	}

	/*
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if ((e.getSource() == sldScaling)) {// && !sldScaling.getModel().getValueIsAdjusting()) {
			setScaleFactor(sldScaling.getValue());
		} else if ((e.getSource() == sldOpacity)) {// && !sldOpacity.getModel().getValueIsAdjusting()) {
			setOpacity(sldOpacity.getValue());
		}
	}

	private void attemptSelectOpacityColor() {
		setOpacityColor(JColorChooser.showDialog(this, Resources.getString(Resources.TITLE_CHOOSE_OPACITY_COLOR), opacityColor));
	}

	/**
	 * Centers the selection area on the specified point.
	 * 
	 * @param x
	 *          the X coordinate.
	 * @param y
	 *          the Y coordinate.
	 */
	private void centerAreaOnPoint(int x, int y) {
		targetX = x * 100 / scaleFactor;
		targetY = y * 100 / scaleFactor;
		areaX = x - (areaWidth / 2);
		areaY = y - (areaHeight / 2);
		checkAreaBounds();
		pnlImage.repaint();
	}

	private void centerImage() {
		Point position = spImage.getViewport().getViewPosition();
		Dimension size = spImage.getViewport().getExtentSize();
		Dimension viewSize = spImage.getViewport().getViewSize();

		int newViewX;
		int newViewY;

		if (size.width < viewSize.width) {
			newViewX = (areaX + areaWidth / 2) * zoomLevel - (size.width / 2);
			if (newViewX < 0) {
				newViewX = 0;
			} else if (newViewX > (viewSize.getWidth() - size.getWidth())) {
				newViewX = viewSize.width - size.width;
			}
		} else {
			newViewX = position.x;
		}

		if (size.height < viewSize.height) {
			newViewY = (areaY + areaHeight / 2) * zoomLevel - (size.height / 2);
			if (newViewY < 0) {
				newViewY = 0;
			} else if (newViewY > (viewSize.getHeight() - size.getHeight())) {
				newViewY = viewSize.height - size.height;
			}
		} else {
			newViewY = position.y;
		}

		spImage.getViewport().setViewPosition(new Point(newViewX, newViewY));
	}

	private void checkAreaBounds() {
		int currentHorizontalLimit = (imageWidth * scaleFactor / 100) - areaWidth;
		int currentVerticalLimit = (imageHeight * scaleFactor / 100) - areaHeight;
		boolean changed = false;

		if (areaX < 0) {
			areaX = 0;
			changed = true;
		} else if (areaX > currentHorizontalLimit) {
			areaX = currentHorizontalLimit;
			changed = true;
		}

		if (areaY < 0) {
			areaY = 0;
			changed = true;
		} else if (areaY > currentVerticalLimit) {
			areaY = currentVerticalLimit;
			changed = true;
		}

		if (changed) {
			targetX = (areaX + areaWidth / 2) * 100 / scaleFactor;
			targetY = (areaY + areaHeight / 2) * 100 / scaleFactor;
		}
	}

	private void generateCurrentImage() {
		int currentWidth = imageWidth * scaleFactor / 100;
		int currentHeight = imageHeight * scaleFactor / 100;

		pnlImage.setSize(new Dimension(currentWidth * zoomLevel, currentHeight * zoomLevel));
		spImage.getViewport().revalidate();
		spImage.getViewport().repaint();
	}

	private Component getButtonComponent() {
		btnCapture = new JButton(Resources.getString(Resources.BUTTON_CAPTURE));
		btnCancel = new JButton(Resources.getString(Resources.BUTTON_CANCEL));

		JPanel p = new JPanel(new BorderLayout());
		p.add(btnCapture, BorderLayout.CENTER);
		if (showCancelButton) {
			p.add(btnCancel, BorderLayout.SOUTH);
		}

		return p;
	}

	private Component getImageComponent() {
		pnlImage = new ImagePanel();
		pnlImage.addMouseListener(this);
		pnlImage.addMouseMotionListener(this);

		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		p.add(pnlImage, gbc);

		spImage = new JScrollPane(p);
		return spImage;
	}

	private Component getOpacityComponent() {
		sldOpacity = new JSlider(OPACITY_MIN, OPACITY_MAX, opacity);
		sldOpacity.setMajorTickSpacing(10);
		sldOpacity.setPaintTicks(true);
		sldOpacity.addChangeListener(this);

		tfOpacityValue = new JTextField();
		tfOpacityValue.setText(String.valueOf(opacity));
		tfOpacityValue.setHorizontalAlignment(JTextField.RIGHT);
		tfOpacityValue.setPreferredSize(new Dimension(40, 20));
		tfOpacityValue.addActionListener(this);

		btnOpacityColor = new JButton();
		btnOpacityColor.addActionListener(this);
		updateOpacityColorButton();

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.TITLE_OPACITY)));
		p.add(sldOpacity, BorderLayout.CENTER);
		p.add(tfOpacityValue, BorderLayout.EAST);
		p.add(btnOpacityColor, BorderLayout.SOUTH);

		return p;
	}

	private Component getOperationsPanel() {
		btnRotateLeft = new JButton(Resources.getIcon("rotate-left.png"));
		btnRotateLeft.addActionListener(this);

		btnCenter = new JButton(Resources.getIcon("center.png"));
		btnCenter.addActionListener(this);

		btnRotateRight = new JButton(Resources.getIcon("rotate-right.png"));
		btnRotateRight.addActionListener(this);

		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.TITLE_OPERATIONS)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 0);

		p.add(btnRotateLeft, gbc);
		gbc.gridx++;
		p.add(btnCenter, gbc);
		gbc.gridx++;
		p.add(btnRotateRight, gbc);

		return p;
	}

	private Component getScalingComponent() {
		sldScaling = new JSlider(SCALE_FACTOR_MIN, SCALE_FACTOR_MAX, scaleFactor);
		sldScaling.setMajorTickSpacing(100);
		sldScaling.setPaintTicks(true);
		sldScaling.addChangeListener(this);

		tfScalingValue = new JTextField();
		tfScalingValue.setText(String.valueOf(scaleFactor));
		tfScalingValue.setHorizontalAlignment(JTextField.TRAILING);
		tfScalingValue.setPreferredSize(new Dimension(40, 20));
		tfScalingValue.addActionListener(this);

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.TITLE_SCALING)));
		p.add(sldScaling, BorderLayout.CENTER);
		p.add(tfScalingValue, BorderLayout.EAST);

		return p;
	}

	private Component getZoomComponent() {
		cboZoomLevel = new JComboBox();
		for (int i = 1; i <= 4; i++) {
			cboZoomLevel.addItem(new Integer(i));
		}
		cboZoomLevel.setPreferredSize(new Dimension(100, 20));
		cboZoomLevel.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setText(value.toString() + "x");
				return this;
			}
		});
		cboZoomLevel.addItemListener(this);

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.TITLE_ZOOM)));
		p.add(cboZoomLevel, BorderLayout.CENTER);

		return p;
	}

	private Color invertColor(Color color) {
		return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
	}

	private void rotateImage(int angle) {
		BufferedImage bi = new BufferedImage(imageHeight, imageWidth, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = bi.createGraphics();
		int xRot = bi.getWidth() / 2;
		int yRot = bi.getHeight() / 2;
		int x = (bi.getWidth() - imageWidth) / 2;
		int y = (bi.getHeight() - imageHeight) / 2;

		AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(angle), xRot, yRot);
		g2d.setTransform(rotation);
		g2d.drawImage(image, x, y, null);
		g2d.dispose();

		image = bi;
		imageWidth = image.getWidth(null);
		imageHeight = image.getHeight(null);

		generateCurrentImage();
	}

	private void updateOpacityColorButton() {
		btnOpacityColor.setBackground(opacityColor);
		btnOpacityColor.setForeground(invertColor(opacityColor));
		String colorString = Integer.toHexString(opacityColor.getRGB() & 0x00FFFFFF);
		while (colorString.length() < 6) {
			colorString = "0" + colorString;
		}
		btnOpacityColor.setText(colorString.toUpperCase());
	}

	private class ImagePanel extends JPanel {

		/*
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (image != null) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, imageWidth, imageHeight, null);

				Color opColor = new Color(opacityColor.getRed(), opacityColor.getGreen(), opacityColor.getBlue(), (100 - opacity) * 255 / 100);

				g2d.setColor(opColor);
				if (areaX > 0) {
					g2d.fillRect(0, 0, areaX * zoomLevel, getHeight() * zoomLevel);
				}
				if ((areaX + areaWidth) * zoomLevel < getWidth()) {
					g2d.fillRect((areaX + areaWidth) * zoomLevel, 0, getWidth(), getHeight());
				}
				if (areaY > 0) {
					g2d.fillRect(areaX * zoomLevel, 0, areaWidth * zoomLevel, areaY * zoomLevel);
				}
				if ((areaY + areaHeight) * zoomLevel < getHeight()) {
					g2d.fillRect(areaX * zoomLevel, (areaY + areaHeight) * zoomLevel, areaWidth * zoomLevel, getHeight() * zoomLevel);
				}

				g2d.setColor(invertColor(opacityColor));
				g2d.drawRect(areaX * zoomLevel - 1, areaY * zoomLevel - 1, areaWidth * zoomLevel + 1, areaHeight * zoomLevel + 1);
			}
		}

		/*
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		public Dimension getPreferredSize() {
			return getSize();
		}

		/*
		 * @see javax.swing.JComponent#getMinimumSize()
		 */
		public Dimension getMinimumSize() {
			return getSize();
		}

		/*
		 * @see javax.swing.JComponent#getMaximumSize()
		 */
		public Dimension getMaximumSize() {
			return getSize();
		}

	}

}
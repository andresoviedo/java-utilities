package org.andresoviedo.util.swing.jimageselector;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.andresoviedo.util.swing.jimageselector.resources.Resources;


public class Test extends JFrame {
	public Test() {
		super("Image selector tester");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image testImage = null;
		try {
			testImage = Resources.getIcon("test.jpg").getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final JImageSelector is = new JImageSelector(testImage);
		is.addCaptureListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JLabel lbl = new JLabel(new ImageIcon(is.getSelectedImage()));

				JDialog dlg = new JDialog(Test.this, "Result image", true);
				dlg.getContentPane().setLayout(new BorderLayout());
				dlg.getContentPane().add(lbl, BorderLayout.CENTER);
				dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dlg.pack();
				dlg.setLocationRelativeTo(Test.this);
				dlg.setVisible(true);
			}
		});

		// Layout components
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(is, BorderLayout.CENTER);

		setSize(800, 600);
		validate();
		setVisible(true);
	}

	public static void main(String[] args) {
		new Test();
	}

}

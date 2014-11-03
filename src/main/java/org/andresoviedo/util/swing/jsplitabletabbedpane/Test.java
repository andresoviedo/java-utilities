package org.andresoviedo.util.swing.jsplitabletabbedpane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Test {

	public static void main(String[] args) {
		final JSplitableTabbedPane tp = new JSplitableTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT, false);

		JLabel l;
		for (int i = 0; i < 10; i++) {
			l = new JLabel("Tab " + i);
			l.setName("tab" + i);
			tp.addTab("Tab " + i, l);
		}

		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// System.out.println(tp.generate());
			}

		});
		JFrame f = new JFrame("Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel p = (JPanel) f.getContentPane();
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p.add(tp);
		p.add(btnTest, BorderLayout.SOUTH);

		f.setSize(new Dimension(800, 600));
		f.setVisible(true);
	}

}

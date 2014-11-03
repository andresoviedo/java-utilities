package org.andresoviedo.util.swing.jchecktree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;

public class TristateCheckBox extends JCheckBox {

	private final TristateDecorator model;

	public TristateCheckBox() {
		this(null);
	}

	public TristateCheckBox(String text) {
		this(text, null);
	}

	public TristateCheckBox(String text, Boolean initial) {
		this(text, null, initial);
	}

	public TristateCheckBox(String text, Icon icon, Boolean initial) {
		super(text, icon);
		super.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				grabFocus();
				model.nextState();
			}

		});
		ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				grabFocus();
				model.nextState();
			}

		});
		map.put("released", null);
		SwingUtilities.replaceUIActionMap(this, map);
		model = new TristateDecorator(getModel());
		setModel(model);
		setState(initial);
	}

	public void addMouseListener(MouseListener mouselistener) {
	}

	public void setState(Boolean state) {
		model.setState(state);
	}

	public Boolean getState() {
		return model.getState();
	}

	private class TristateDecorator implements ButtonModel {

		private final ButtonModel other;

		private TristateDecorator(ButtonModel other) {
			this.other = other;
		}

		private void setState(Boolean state) {
			if (state == Boolean.FALSE) {
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			} else if (state == Boolean.TRUE) {
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else {
				other.setArmed(true);
				setPressed(true);
				setSelected(true);
			}
		}

		private Boolean getState() {
			if (isSelected() && !isArmed()) {
				return Boolean.TRUE;
			}
			if (isSelected() && isArmed()) {
				return null;
			} else {
				return Boolean.FALSE;
			}
		}

		private void nextState() {
			Boolean current = getState();
			if (current == Boolean.FALSE) {
				setState(Boolean.TRUE);
			} else if (current == Boolean.TRUE) {
				setState(null);
			} else if (current == null) {
				setState(Boolean.FALSE);
			}
		}

		public void setArmed(boolean flag) {
		}

		public boolean isFocusTraversable() {
			return isEnabled();
		}

		public void setEnabled(boolean b) {
			other.setEnabled(b);
		}

		public boolean isArmed() {
			return other.isArmed();
		}

		public boolean isSelected() {
			return other.isSelected();
		}

		public boolean isEnabled() {
			return other.isEnabled();
		}

		public boolean isPressed() {
			return other.isPressed();
		}

		public boolean isRollover() {
			return other.isRollover();
		}

		public void setSelected(boolean b) {
			other.setSelected(b);
		}

		public void setPressed(boolean b) {
			other.setPressed(b);
		}

		public void setRollover(boolean b) {
			other.setRollover(b);
		}

		public void setMnemonic(int key) {
			other.setMnemonic(key);
		}

		public int getMnemonic() {
			return other.getMnemonic();
		}

		public void setActionCommand(String s) {
			other.setActionCommand(s);
		}

		public String getActionCommand() {
			return other.getActionCommand();
		}

		public void setGroup(ButtonGroup group) {
			other.setGroup(group);
		}

		public void addActionListener(ActionListener l) {
			other.addActionListener(l);
		}

		public void removeActionListener(ActionListener l) {
			other.removeActionListener(l);
		}

		public void addItemListener(ItemListener l) {
			other.addItemListener(l);
		}

		public void removeItemListener(ItemListener l) {
			other.removeItemListener(l);
		}

		public void addChangeListener(ChangeListener l) {
			other.addChangeListener(l);
		}

		public void removeChangeListener(ChangeListener l) {
			other.removeChangeListener(l);
		}

		public Object[] getSelectedObjects() {
			return other.getSelectedObjects();
		}
	}

}
package org.andresoviedo.util.swing.jsplitabletabbedpane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * JSplitableTabbedPane.
 * 
 */
public class JSplitableTabbedPane extends JPanel {

	/**
	 * An instance of TabbedPaneSplitter.
	 */
	private Splitter listener;

	/**
	 * The default tab placement used when creating a new tabbed pane (one of JTabbedPane.TOP, JTabbedPane.BOTTOM, JTabbedPane.LEFT, or
	 * JTabbedPane.RIGHT).
	 */
	private int defaultTabPlacement;

	/**
	 * The default tab layout policy used when creating a new tabbed pane (one of JTabbedPane.WRAP_TAB_LAYOUT or
	 * JTabbedPane.SCROLL_TAB_LAYOUT).
	 */
	private int defaultTabLayoutPolicy;

	/**
	 * Indicates if split panes have to be used when splitting tabbed panes.
	 */
	private boolean useSplitPanes;

	/**
	 * Indicates if a popup menu
	 */
	private boolean showTabPopupMenu;

	/**
	 * Creates a new JSplittableTabbedPane with JTabbedPane.TOP and JTabbedPane.WRAP_TAB_LAYOUT as the default values for the tab placement
	 * and the tab layout policy. Split panes will be used.
	 */
	public JSplitableTabbedPane() {
		this(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT, true);
	}

	/**
	 * Creates a new JSplittableTabbedPane with the specified values as the default values for the tab placement and the tab layout policy.
	 * Notice that the split pane won't work if the layout policy is JTabbedPane.SCROLL_TAB_LAYOUT and a JVM version below 1.5 is used.
	 * 
	 * @param defaultTabPlacement
	 *            the default tab placement. Tab placement may be either: JTabbedPane.TOP, JTabbedPane.BOTTOM, JTabbedPane.LEFT, or
	 *            JTabbedPane.RIGHT.
	 * @param defaultTabLayoutPolicy
	 *            the default tab layout policy. Tab layout policy may be either: JTabbedPane.WRAP_TAB_LAYOUT or
	 *            JTabbedPane.SCROLL_TAB_LAYOUT.
	 * @param useSplitPanes
	 *            indicates if split panes have to be used when splitting tabbed panes.
	 */
	public JSplitableTabbedPane(int defaultTabPlacement, int defaultTabLayoutPolicy, boolean useSplitPanes) {
		this.defaultTabPlacement = defaultTabPlacement;
		this.defaultTabLayoutPolicy = defaultTabLayoutPolicy;
		this.useSplitPanes = useSplitPanes;

		setLayout(new BorderLayout());

		listener = new Splitter(this);
	}

	/**
	 * Returns the the default tab layout policy value.
	 * 
	 * @return the default tab layout policy.
	 */
	public int getDefaultTabLayoutPolicy() {
		return defaultTabLayoutPolicy;
	}

	/**
	 * Sets the default tab layout policy (one of JTabbedPane.WRAP_TAB_LAYOUT or JTabbedPane.SCROLL_TAB_LAYOUT). This method will affect
	 * newly created tabbed panes, not the existing ones.
	 * 
	 * @param defaultTabLayoutPolicy
	 *            The new default tab layout policy.
	 */
	public void setDefaultTabLayoutPolicy(int defaultTabLayoutPolicy) {
		this.defaultTabLayoutPolicy = defaultTabLayoutPolicy;
	}

	/**
	 * Returns the default tab placement value.
	 * 
	 * @return the default tab placement.
	 */
	public int getDefaultTabPlacement() {
		return defaultTabPlacement;
	}

	/**
	 * Sets the default tab placement (one of JTabbedPane.TOP, JTabbedPane.BOTTOM, JTabbedPane.LEFT, or JTabbedPane.RIGHT). This method will
	 * affect newly created tabbed panes, not the existing ones.
	 * 
	 * @param defaultTabPlacement
	 *            The new default tab placement.
	 */
	public void setDefaultTabPlacement(int defaultTabPlacement) {
		this.defaultTabPlacement = defaultTabPlacement;
	}

	/**
	 * Returns if split panes have to be used when splitting tabbed panes.
	 * 
	 * @return <code>true</code> if split panes have to be used when splitting tabbed panes.
	 */
	public boolean isUseSplitPanes() {
		return useSplitPanes;
	}

	/**
	 * Indicates whether the tab popup menu has to be shown when the user right clicks on a tab.
	 * 
	 * @return <code>true</code> if the popup has to be shown, <code>false</code> otherwise.
	 */
	public boolean isShowTabPopupMenu() {
		return showTabPopupMenu;
	}

	/**
	 * Sets whether the tab popup menu has to be shown when the user right clicks on a tab.
	 * 
	 * @param showTabPopupMenu
	 *            <code>true</code> if the popup has to be shown, <code>false</code> otherwise.
	 */
	public void setShowTabPopupMenu(boolean showTabPopupMenu) {
		this.showTabPopupMenu = showTabPopupMenu;
	}

	/**
	 * Adds a new tab to the first available tabbed pane. If no tabbed pane has already been created, a new one will be.
	 * 
	 * @param title
	 *            the title to be displayed in this tab
	 * @param component
	 *            the component to be displayed when this tab is clicked
	 */
	public void addTab(String title, Component component) {
		addTab(title, null, component, null);
	}

	/**
	 * Adds a new tab to the first available tabbed pane. If no tabbed pane has already been created, a new one will be.
	 * 
	 * @param title
	 *            the title to be displayed in this tab
	 * @param icon
	 *            the icon to be displayed in this tab
	 * @param component
	 *            the component to be displayed when this tab is clicked
	 */
	public void addTab(String title, Icon icon, Component component) {
		addTab(title, icon, component, null);
	}

	/**
	 * Adds a new tab to the first available tabbed pane. If no tabbed pane has already been created, a new one will be.
	 * 
	 * @param title
	 *            the title to be displayed in this tab
	 * @param icon
	 *            the icon to be displayed in this tab
	 * @param component
	 *            the component to be displayed when this tab is clicked
	 * @param tip
	 *            the tooltip to be displayed for this tab
	 */
	public void addTab(String title, Icon icon, Component component, String tip) {
		JTabbedPane tabbedPane = getFirstAvailableTabbedPane(this);
		if (tabbedPane == null) {
			tabbedPane = createNewTabbedPane();
			add(tabbedPane, BorderLayout.CENTER);
		}

		// Add the tab.
		tabbedPane.addTab(title, icon, component, tip);
	}

	/**
	 * Adds a new tab to the first available tabbed pane, at the specified index. If no tabbed pane has already been created, a new one will
	 * be. Notice that if the specified index is not valid, the new tab will be inserted at the end.
	 * 
	 * @param title
	 *            the title to be displayed in this tab
	 * @param component
	 *            the component to be displayed when this tab is clicked
	 * @param index
	 *            the position to insert this new tab (0-based).
	 */
	public void insertTab(String title, Component component, int index) {
		insertTab(title, null, component, null, index);
	}

	/**
	 * Adds a new tab to the first available tabbed pane, at the specified index. If no tabbed pane has already been created, a new one will
	 * be. Notice that if the specified index is not valid, the new tab will be inserted at the end.
	 * 
	 * @param title
	 *            the title to be displayed in this tab
	 * @param icon
	 *            the icon to be displayed in this tab
	 * @param component
	 *            the component to be displayed when this tab is clicked
	 * @param index
	 *            the position to insert this new tab (0-based).
	 */
	public void insertTab(String title, Icon icon, Component component, int index) {
		insertTab(title, icon, component, null, index);
	}

	/**
	 * Adds a new tab to the first available tabbed pane, at the specified index. If no tabbed pane has already been created, a new one will
	 * be. Notice that if the specified index is not valid, the new tab will be inserted at the end.
	 * 
	 * @param title
	 *            the title to be displayed in this tab
	 * @param icon
	 *            the icon to be displayed in this tab
	 * @param component
	 *            the component to be displayed when this tab is clicked
	 * @param tip
	 *            the tooltip to be displayed for this tab
	 * @param index
	 *            the position to insert this new tab (0-based).
	 */
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		JTabbedPane tabbedPane = getFirstAvailableTabbedPane(this);
		if (tabbedPane == null) {
			tabbedPane = createNewTabbedPane();
			add(tabbedPane, BorderLayout.CENTER);
		}

		// Check whether the index is valid or not.
		if ((index < 0) || (index > tabbedPane.getTabCount())) {
			index = tabbedPane.getTabCount();
		}

		// Insert the tab.
		tabbedPane.insertTab(title, icon, component, tip, index);
	}

	/**
	 * Returns the tabbed parent which is the ancestor of the component. If the component is not in the hierarchy, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param c
	 *            the component.
	 * 
	 * @return the tabbed parent which is the ancestor of the component.
	 */
	public static JTabbedPane getTabbedPaneForComponent(Component c) {
		if (c == null) {
			return null;
		}
		if (c instanceof JTabbedPane) {
			return (JTabbedPane) c;
		}
		return getTabbedPaneForComponent(c.getParent());
	}

	/**
	 * Returns the tab index of the specified component. The component should have a JTabbedPane as an ancestor, the depth doesn't matter.
	 * 
	 * @param c
	 *            the component.
	 * @return the tab index of the specified component.
	 */
	public static int getTabIndexForComponent(Component c) {
		JTabbedPane tp = getTabbedPaneForComponent(c);
		if (tp == null) {
			return -1;
		}

		for (int i = 0; i < tp.getTabCount(); i++) {
			if (SwingUtilities.isDescendingFrom(c, tp.getComponentAt(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Select the tab holding the specified component.
	 * 
	 * @param c
	 *            the component.
	 */
	public static void selectComponentTab(Component c) {
		JTabbedPane tp = getTabbedPaneForComponent(c);
		if (tp != null) {
			int index = getTabIndexForComponent(c);
			if (index != -1) {
				tp.setSelectedIndex(index);
			}
		}
	}

	/*
	 * @see javax.swing.JPanel#updateUI()
	 */
	public void updateUI() {
		super.updateUI();
		// The popup menu is not in the component hierarchy, so it has to be updated manually.
		if ((listener != null) && (listener.getPopupMenu() != null)) {
			SwingUtilities.updateComponentTreeUI(listener.getPopupMenu());
		}
	}

	/**
	 * Creates a new tabbed pane to add moved tabs in it, and adds the appropriate listeners.
	 * 
	 * @return the newly created tabbed pane.
	 */
	private JTabbedPane createNewTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane(defaultTabPlacement, defaultTabLayoutPolicy);
		tabbedPane.addContainerListener(listener);
		tabbedPane.addMouseListener(listener);
		tabbedPane.addMouseMotionListener(listener);

		return tabbedPane;
	}

	/**
	 * Creates a new tabbed pane and adds a tab at index 'index' in the source tabbed pane.
	 * 
	 * @param tabbedPane
	 *            the source tabbed pane to get the component from.
	 * @param index
	 *            the index of the tab to be moved.
	 * 
	 * @return the new tabbed pane.
	 */
	JTabbedPane createNewTabbedPane(JTabbedPane tabbedPane, int index) {
		int src = index;
		int dst = 0;

		Component comp = tabbedPane.getComponentAt(src);
		String label = tabbedPane.getTitleAt(src);
		Icon icon = tabbedPane.getIconAt(src);
		Icon iconDis = tabbedPane.getDisabledIconAt(src);
		String tooltip = tabbedPane.getToolTipTextAt(src);
		boolean enabled = tabbedPane.isEnabledAt(src);
		int keycode = tabbedPane.getMnemonicAt(src);
		int mnemonicLoc = tabbedPane.getDisplayedMnemonicIndexAt(src);
		Color fg = tabbedPane.getForegroundAt(src);
		Color bg = tabbedPane.getBackgroundAt(src);

		tabbedPane.remove(src);

		JTabbedPane tp = createNewTabbedPane();

		tp.insertTab(label, icon, comp, tooltip, dst);
		tp.setDisabledIconAt(dst, iconDis);
		tp.setEnabledAt(dst, enabled);
		tp.setMnemonicAt(dst, keycode);
		tp.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
		tp.setForegroundAt(dst, fg);
		tp.setBackgroundAt(dst, bg);

		return tp;
	}

	/**
	 * Returns the first tabbed pane available to add a new tab.
	 * 
	 * @return the first tabbed pane available to add a new tab.
	 */
	private JTabbedPane getFirstAvailableTabbedPane(Container c) {
		if (c instanceof JTabbedPane) {
			return (JTabbedPane) c;
		}

		JTabbedPane result;
		for (int i = 0; i < c.getComponentCount(); i++) {
			result = getFirstAvailableTabbedPane((Container) c.getComponent(i));
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	/**
	 * Moves a tab from one tabbed pane to another.
	 * 
	 * @param tp1
	 *            the source tabbed pane.
	 * @param tp2
	 *            the destination tabbed pane.
	 * @param index1
	 *            the source index.
	 * @param index2
	 *            the destination index.
	 * @return <code>true</code> if the move has been performed, <code>false</code> otherwise.
	 */
	boolean moveTab(JTabbedPane tp1, JTabbedPane tp2, int index1, int index2) {
		// Check nulls.
		if (tp1 == null || tp2 == null) {
			return false;
		}

		// Check index 1.
		if (index1 < 0 || index1 > tp1.getTabCount()) {
			return false;
		}

		// Check if the moving is necessary.
		if (tp1.equals(tp2) && (index1 == index2)) {
			return false;
		}
		if (tp1.equals(tp2) && (index2 == -1)) {
			return false;
		}

		// If index2 is out of bounds, assign a valid value.
		if (index2 < 0 || index2 > tp2.getTabCount()) {
			index2 = tp2.getTabCount();
		}

		// Get the properties of the selected source tab.
		Component comp = tp1.getComponentAt(index1);
		String label = tp1.getTitleAt(index1);
		Icon icon = tp1.getIconAt(index1);
		Icon iconDis = tp1.getDisabledIconAt(index1);
		String tooltip = tp1.getToolTipTextAt(index1);
		boolean enabled = tp1.isEnabledAt(index1);
		int keycode = tp1.getMnemonicAt(index1);
		int mnemonicLoc = tp1.getDisplayedMnemonicIndexAt(index1);
		Color fg = tp1.getForegroundAt(index1);
		Color bg = tp1.getBackgroundAt(index1);

		tp1.remove(index1);

		if (tp1.equals(tp2)) {
			if (index2 < 0 || index2 > tp2.getTabCount()) {
				index2--;
			}
		}

		// Add the tab to the destination tabbed pane.
		tp2.insertTab(label, icon, comp, tooltip, index2);
		tp2.setDisabledIconAt(index2, iconDis);
		tp2.setEnabledAt(index2, enabled);
		tp2.setMnemonicAt(index2, keycode);
		tp2.setDisplayedMnemonicIndexAt(index2, mnemonicLoc);
		tp2.setForegroundAt(index2, fg);
		tp2.setBackgroundAt(index2, bg);

		// Select the pane.
		tp2.setSelectedIndex(index2);

		// Repaint.
		repaint();

		return true;
	}

	/**
	 * Adds the tab from tp1 at tabIndex to a new tabbed pane. Tp2 and the new tabbed pane are added to a new panel with a gridlayout as the
	 * layout manager. The new panel replaces tp2 inside its parent.
	 * 
	 * @param tp1
	 *            the source tabbed pane.
	 * @param tp2
	 *            the destination tabbed pane.
	 * @param tabIndex
	 *            the index of the tab to be moved.
	 * @param position
	 *            the position of the new tab inside the destination tabbed pane (LEFT, RIGHT, TOP, BOTTOM).
	 * @return <code>true</code> if the move has been performed, <code>false</code> otherwise.
	 */
	boolean splitTab(JTabbedPane tp1, JTabbedPane tp2, int tabIndex, int position) {
		// Check nulls.
		if (tp1 == null || tp2 == null) {
			return false;
		}

		// Check index 1.
		if (tabIndex < 0 || tabIndex > tp1.getTabCount()) {
			return false;
		}

		if (useSplitPanes) {
			splitTabUsingSplitPane(tp1, tp2, tabIndex, position);
		} else {
			splitTabUsingPanel(tp1, tp2, tabIndex, position);
		}

		return true;
	}

	void splitTabUsingPanel(JTabbedPane tp1, JTabbedPane tp2, int tabIndex, int position) {
		// Create a new tabbed pane with the moved tab.
		JTabbedPane tabbedPane = createNewTabbedPane(tp1, tabIndex);

		// Configure the layout that will hold both the new tabbed pane and the
		// destination one.
		GridLayout layout;
		if (position == SplitterConstants.SPLIT_LEFT || position == SplitterConstants.SPLIT_RIGHT) {
			layout = new GridLayout(1, 0, 5, 5);
		} else {
			layout = new GridLayout(0, 1, 5, 5);
		}

		// Get the index of the destination tabbed pane inside its parent.
		Container parent = tp2.getParent();

		int index = getComponentIndexInParent(tp2);

		// Create the panel and add the components in the correct order.
		JPanel p = new JPanel(layout);
		if (position == SplitterConstants.SPLIT_LEFT || position == SplitterConstants.SPLIT_TOP) {
			p.add(tabbedPane);
			p.add(tp2);
		} else {
			p.add(tp2);
			p.add(tabbedPane);
		}

		// Replace the tabbed pane by the newly created panel.
		parent.add(p, index);

		// Revalidate.
		revalidate();
	}

	void splitTabUsingSplitPane(JTabbedPane tp1, JTabbedPane tp2, int tabIndex, int position) {
		// Create a new tabbed pane with the moved tab.
		JTabbedPane tabbedPane = createNewTabbedPane(tp1, tabIndex);

		// Get the index of the destination tabbed pane inside its parent.
		Container parent = tp2.getParent();

		int index = 0;
		for (; index < parent.getComponentCount(); index++) {
			if (parent.getComponent(index).equals(tp2)) {
				break;
			}
		}

		// Create a new split pane.
		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(false);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);

		switch (position) {
		case SplitterConstants.SPLIT_LEFT:
			splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(tabbedPane);
			splitPane.setRightComponent(tp2);
			break;
		case SplitterConstants.SPLIT_RIGHT:
			splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(tp2);
			splitPane.setRightComponent(tabbedPane);
			break;
		case SplitterConstants.SPLIT_TOP:
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setTopComponent(tabbedPane);
			splitPane.setBottomComponent(tp2);
			break;
		case SplitterConstants.SPLIT_BOTTOM:
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setTopComponent(tp2);
			splitPane.setBottomComponent(tabbedPane);
			break;
		}

		// Replace the tabbed pane by the newly created panel.
		parent.add(splitPane, index);

		// Revalidate.
		revalidate();
	}

	/**
	 * Checks if a tabbed pane stills have tabs in it. If not, it's removed from its parent.
	 * 
	 * @param tabbedPane
	 *            the tabbed pane to check.
	 */
	void checkTabbedPaneHasTabs(JTabbedPane tabbedPane) {
		if (tabbedPane.getTabCount() == 0) {
			Container parent = tabbedPane.getParent();
			if (parent != null) {
				parent.remove(tabbedPane);

				if (useSplitPanes) {
					checkParentIsNecessarySplitPane(parent);
				} else {
					checkParentIsNecessaryPanel(parent);
				}
			}

			// Revalidate.
			revalidate();
			repaint();
		}
	}

	void checkParentIsNecessarySplitPane(Container c) {
		if (c instanceof JSplitPane) {
			JSplitPane splitPane = (JSplitPane) c;

			// Get the index of the split pane inside its parent.
			int index = getComponentIndexInParent(splitPane);

			// Get the component to replace.
			Component child = splitPane.getLeftComponent();
			if (child == null) {
				child = splitPane.getRightComponent();
			}
			c = splitPane.getParent();
			if (c != null) {
				c.remove(splitPane);
				c.add(child, index);
			}
		}
	}

	/**
	 * Checks if the component stills have subcomponents. If not, it gets removed from its parent. This method is recursive.
	 * 
	 * @param c
	 *            the component to check.
	 */
	private void checkParentIsNecessaryPanel(Container c) {
		if (c == null) {
			return;
		}

		if (c.getComponentCount() == 0) {
			Container parent = c.getParent();
			parent.remove(c);
			checkParentIsNecessaryPanel(parent);
		}
	}

	/**
	 * Returns the index of the component inside its parent. Returns -1 if the component has no parent.
	 * 
	 * @param c
	 *            the component which index has to be searched.
	 * 
	 * @return the index of the component inside its parent.
	 */
	private int getComponentIndexInParent(Component c) {
		Container parent = c.getParent();
		if (parent == null) {
			return -1;
		}

		int index = 0;
		for (; index < parent.getComponentCount(); index++) {
			if (parent.getComponent(index).equals(c)) {
				break;
			}
		}
		return index;
	}

}
package org.andresoviedo.util.schedule.api1;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.andresoviedo.util.bean.ScheduleInfoBean;
import org.andresoviedo.util.schedule.api1.TasksScheduler.TaskWrapper;

public class TasksControlPanel extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 5663476032309215442L;

	@SuppressWarnings("unused")
	private static final String PERIOD_STRING[] = { "periodically", "daily", "weekly", "monthly" };
	private static final String DAYS_STRING[] = { "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

	JTable tasksTable = new JTable();
	TasksTableModel tableModel = new TasksTableModel();
	JButton buttonStart = new JButton("Start");
	JButton buttonStop = new JButton("Stop");
	Action startAction, stopAction;

	TasksScheduler scheduler;

	public TasksControlPanel(TasksScheduler scheduler) {
		this.scheduler = scheduler;
		init();
	}

	private void init() {
		setLayout(new BorderLayout());

		// set table
		tasksTable.setModel(tableModel);
		tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tasksTable.getSelectionModel().addListSelectionListener(this);
		try {
			TableColumn tableColumn = tasksTable.getColumnModel().getColumn(1);
			tableColumn.setPreferredWidth(150);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		JScrollPane scrollPane = new JScrollPane(tasksTable);
		add(scrollPane, BorderLayout.CENTER);

		// set buttons
		JPanel panelButtons = new JPanel();
		@SuppressWarnings("unused")
		BoxLayout box = new BoxLayout(panelButtons, BoxLayout.X_AXIS);
		panelButtons.add(Box.createHorizontalGlue());
		panelButtons.add(buttonStart);
		panelButtons.add(buttonStop);
		startAction = new AbstractAction() {
			private static final long serialVersionUID = -2533476136159362029L;

			public void actionPerformed(ActionEvent e) {
				scheduler.run(scheduler.tasks.elementAt(tasksTable.getSelectedRow()).task);
			}
		};
		startAction.putValue(Action.NAME, "Start");
		stopAction = new AbstractAction() {
			private static final long serialVersionUID = -2533476136159362029L;

			public void actionPerformed(ActionEvent e) {
				scheduler.cancel(scheduler.tasks.elementAt(tasksTable.getSelectedRow()).task);
			}
		};
		stopAction.putValue(Action.NAME, "Stop");
		buttonStart.setAction(startAction);
		buttonStop.setAction(stopAction);
		startAction.setEnabled(false);
		stopAction.setEnabled(false);
		add(panelButtons, BorderLayout.SOUTH);
	}

	void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int index = tasksTable.getSelectedRow();
				tableModel.fireTableDataChanged();
				if (index >= 0) {
					tasksTable.setRowSelectionInterval(index, index);
				}
			}
		});
	}

	class TasksTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 2495569478785233225L;

		private final String[] COLUMN_NAMES = { "Tarea", "Planificación", "Estado" };

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		public int getRowCount() {
			return scheduler.tasks.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			TaskWrapper task = scheduler.tasks.getListInterface().get(rowIndex);
			switch (columnIndex) {
			case 0:
				ret = task.taskInfo.getName();
				break;
			case 1:
				ret = getScheduleInfo(task.taskInfo);
				break;
			case 2:
				ret = task.started ? (task.running ? "Running" : "Started") : "Stopped";
				break;
			}
			return ret;
		}
	}

	public static String getScheduleInfo(ScheduleInfoBean taskInfo) {
		StringBuffer ret = new StringBuffer();
		ret.append("Scheduled " + taskInfo.getPeriodType());
		if (taskInfo.getPeriodType() == TasksScheduler.PERIOD_TIME) {
			ret.append(" [" + taskInfo.getPeriodTime() + "]");
		} else if (taskInfo.getPeriodType() == TasksScheduler.PERIOD_WEEKLY) {
			ret.append(" [" + DAYS_STRING[taskInfo.getPeriodWeeklyDay()] + "]");
		} else if (taskInfo.getPeriodType() == TasksScheduler.PERIOD_MONTHLY) {
			ret.append(" [" + taskInfo.getPeriodMonthlyDay() + "]");
		}
		if (taskInfo.getMakeTime() != null) {
			ret.append(" at " + taskInfo.getMakeTime() + ".");
		}
		return ret.toString();
	}

	// ------------------------------------------------------------------------------------------- //

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}

		if (e.getSource() == tasksTable.getSelectionModel()) {
			int idx = tasksTable.getSelectedRow();
			if (idx != -1) {
				TaskWrapper task = scheduler.tasks.getListInterface().get(idx);
				startAction.setEnabled(!task.started);
				stopAction.setEnabled(task.started);
			} else {
				startAction.setEnabled(false);
				stopAction.setEnabled(false);
			}
		}
	}
}

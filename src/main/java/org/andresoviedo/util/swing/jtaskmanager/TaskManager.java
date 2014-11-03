package org.andresoviedo.util.swing.jtaskmanager;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

/**
 * The task manager is a singleton class in charge of executing tasks sequentially (one after another). When a task is added to the list,
 * the task consumer thread is started if it hasn't been started yet and the task manager dialog is displayed. Whether the dialog is
 * displayed as modal depends on the current task being performed.
 * 

 */
public class TaskManager implements Runnable {

	/**
	 * The owner frame for the dialog.
	 */
	private static Frame ownerFrame = null;

	/**
	 * The single instance of this class.
	 */
	private static TaskManager instance;

	/**
	 * The list of tasks.
	 */
	private List<Task> tasks = new Vector<Task>();

	/**
	 * The task manager dialog.
	 */
	private TaskManagerDialog dialog;

	/**
	 * The task consumer thread.
	 */
	private Thread thread;

	/**
	 * Sets the owner frame.
	 * 
	 * @param owner
	 *          the owner frame.
	 */
	public static void setOwnerFrame(Frame owner) {
		TaskManager.ownerFrame = owner;
	}

	/**
	 * Returns the owner frame.
	 * 
	 * @return the owner frame.
	 */
	public static Frame getOwnerFrame() {
		return ownerFrame;
	}

	/**
	 * Don't let anyone instantiate this class.
	 */
	private TaskManager() {
		dialog = new TaskManagerDialog(ownerFrame);
	}

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the single instance of this class.
	 */
	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}

	/**
	 * Adds a task to the list. After adding it, the task manager will start its task consumer thread, if it's not running.
	 * 
	 * @param task
	 *          the task to be added.
	 */
	public void addTask(Task task) {
		if (task == null) {
			throw new IllegalArgumentException("The task cannot be null.");
		}

		if (tasks.contains(task)) {
			throw new IllegalArgumentException("A task cannot be added twice.");
		}

		// Add the task to the list.
		tasks.add(task);

		// Notify.
		synchronized (tasks) {
			tasks.notify();
		}
	}

	/**
	 * Gets the task list as an array.
	 * 
	 * @return the task list as an array.
	 */
	public Task[] getTasks() {
		return (Task[]) tasks.toArray(new Task[tasks.size()]);
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			while (true) {
				// If we have no tasks, wait.
				synchronized (tasks) {
					while (tasks.size() == 0) {
						tasks.wait();
					}
				}

				// Update the view.
				refreshDialog();

				// Show the dialog.
				showDialog();

				// Task execution.
				Task task = null;

				// Execute all tasks.
				while (tasks.size() > 0) {
					// Get the first task to be executed.
					task = (Task) tasks.get(0);

					// Configure the dialog (modality, etc).
					configureDialog(task);

					// Execute the task and wait for it to complete.
					try {
						task.execute();
						task.join();
					} catch (InterruptedException e) {
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// Remove the task, now that it's finished.
						tasks.remove(0);
					}
				}

				// No more tasks, hide the dialog if possible.
				hideDialogWhenPossible();
			}
		} catch (InterruptedException e) {
		}

		// Task manager stopped, so hide the dialog.
		hideDialog();
	}

	/**
	 * Starts the task manager. Task consuming process will be started.
	 */
	public synchronized void start() {
		if ((thread == null) || !thread.isAlive()) {
			thread = new Thread(this);
			thread.setName("Task manager thread");
			thread.start();
		}
	}

	/**
	 * Stops the task manager. Task consuming process will be stopped. The task in progress will run until completion, and the following won't
	 * be executed.
	 */
	public synchronized void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * Refreshes the view. This code executes in the event-dispatching thread.
	 */
	private void refreshDialog() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					dialog.refresh();
				}
			});
		} catch (InterruptedException e) {
		} catch (InvocationTargetException e) {
		}
	}

	private void configureDialog(final Task task) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dialog.configure(task);
			}
		});
	}

	/**
	 * Shows the task manager dialog. This code executes in the event-dispatching thread.
	 */
	public void showDialog() {
		if (SwingUtilities.isEventDispatchThread()) {
			dialog.setVisible(true);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dialog.setVisible(true);
				}
			});
		}
	}

	/**
	 * Hides the task manager dialog. This code executes in the event-dispatching thread.
	 */
	private void hideDialog() {
		if (SwingUtilities.isEventDispatchThread()) {
			dialog.setVisible(false);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dialog.setVisible(false);
				}
			});
		}
	}

	/**
	 * Hides the task manager dialog. This code executes in the event-dispatching thread.
	 */
	private void hideDialogWhenPossible() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dialog.hideWhenPossible();
			}
		});
	}

}
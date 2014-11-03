package org.andresoviedo.util.swing.jtaskmanager;

import javax.swing.Icon;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * A task is intended to perform certain actions in a dedicate thread (using TaskManager). Some propeties can be set during its execution
 * (such as the progress and the message) to let the user know what it's doing.
 * 

 */
public abstract class Task implements Runnable {

	/**
	 * Bound property name for <code>canCancel</code>.
	 */
	public static final String CAN_CANCEL_PROPERTY = "canCancel";

	/**
	 * Bound property name for <code>description</code>.
	 */
	public static final String DESCRIPTION_PROPERTY = "description";

	/**
	 * Bound property name for <code>indeterminate</code>.
	 */
	public static final String INDETERMINATE_PROPERTY = "indeterminate";

	/**
	 * Bound property name for <code>icon</code>.
	 */
	public static final String ICON_PROPERTY = "icon";

	/**
	 * Bound property name for <code>message</code>.
	 */
	public static final String MESSAGE_PROPERTY = "message";

	/**
	 * Bound property name for <code>modal</code>.
	 */
	public static final String MODAL_PROPERTY = "modal";

	/**
	 * Bound property name for <code>progress</code>.
	 */
	public static final String PROGRESS_PROPERTY = "progress";

	/**
	 * The property change support to fire property change events.
	 */
	private SwingPropertyChangeSupport pcs;

	/**
	 * The description of the task.
	 */
	private String description;

	/**
	 * A message describing the current action being performed by the task.
	 */
	private String message;

	/**
	 * Indicates if the task can be cancelled or not.
	 */
	private boolean canCancel;

	/**
	 * Indicates if the progress is indeterminate or not.
	 */
	private boolean indeterminate;

	/**
	 * Indicates if the task is modal or not.
	 */
	private boolean modal;

	/**
	 * The completion percent (from 0 to 100).
	 */
	private int progress;

	/**
	 * An icon associated to the task.
	 */
	private Icon icon;

	/**
	 * An object encapsulating the thread to use to execute the task.
	 */
	private ThreadVar threadVar;

	/**
	 * Constructs a new task.
	 * 
	 * @param description
	 *          the name of the task.
	 */
	public Task(String description) {
		this(description, false);
	}

	/**
	 * Constructs a new task.
	 * 
	 * @param description
	 *          the name of the task.
	 * @param canCancel
	 *          <code>true</code> is the task can be cancelled, <code>false</code> otherwise.
	 */
	public Task(String description, boolean canCancel) {
		this(description, canCancel, true);
	}

	/**
	 * Constructs a new task.
	 * 
	 * @param description
	 *          the name of the task.
	 * @param canCancel
	 *          <code>true</code> is the task can be cancelled, <code>false</code> otherwise.
	 * @param modal
	 *          <code>true</code> is the task is modal, <code>false</code> otherwise.
	 */
	public Task(String description, boolean canCancel, boolean modal) {
		this.description = description;
		this.canCancel = canCancel;
		this.modal = modal;
		this.pcs = new SwingPropertyChangeSupport(this);
	}

	/**
	 * Gets the property change support to add or remove property change listeners to this task.
	 * 
	 * @return the property change support.
	 */
	public SwingPropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}

	/**
	 * Indicates whether this task is can be cancelled or not.
	 * 
	 * @return <code>true</code> if this task can be cancelled, <code>false</code> otherwise.
	 */
	public boolean isCanCancel() {
		return canCancel;
	}

	/**
	 * Sets whether this task can be cancelled or not.
	 * 
	 * @param canCancel
	 *          <code>true</code> if this task can be cancelled, <code>false</code> otherwise.
	 */
	public void setCanCancel(boolean canCancel) {
		if (this.canCancel != canCancel) {
			boolean oldValue = this.canCancel;
			this.canCancel = canCancel;
			pcs.firePropertyChange(CAN_CANCEL_PROPERTY, oldValue, canCancel);
		}
	}

	/**
	 * Gets the name of the task.
	 * 
	 * @return the name of the task.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the task.
	 * 
	 * @param description
	 *          the new description.
	 */
	public void setDescription(String description) {
		if (this.description != description) {
			Object oldValue = this.description;
			this.description = description;
			pcs.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, description);
		}
	}

	/**
	 * Returns the icon associated with this task.
	 * 
	 * @return the icon associated with this task.
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Sets the icon associated with this task.
	 * 
	 * @param icon
	 *          the icon associated with this task.
	 */
	public void setIcon(Icon icon) {
		if (this.icon != icon) {
			Object oldValue = this.icon;
			this.icon = icon;
			pcs.firePropertyChange(ICON_PROPERTY, oldValue, icon);
		}
	}

	/**
	 * Returns whether the progress is indeterminate or not.
	 * 
	 * @return <code>true</code> if the progress is indeterminate, <code>false</code> otherwise.
	 */
	public boolean isIndeterminate() {
		return indeterminate;
	}

	/**
	 * Sets whether the progress is indeterminate or not.
	 * 
	 * @param indeterminate
	 *          <code>true</code> if the progress is indeterminate, <code>false</code> otherwise.
	 */
	public void setIndeterminate(boolean indeterminate) {
		if (this.indeterminate != indeterminate) {
			boolean oldValue = this.indeterminate;
			this.indeterminate = indeterminate;
			pcs.firePropertyChange(INDETERMINATE_PROPERTY, oldValue, indeterminate);
		}
	}

	/**
	 * Gets the message describing the current action being performed by this task. This may change at any time.
	 * 
	 * @return the message of this task.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message describing the current action being performed by this task.
	 * 
	 * @param message
	 *          the message of this task.
	 */
	public void setMessage(String message) {
		if (this.message != message) {
			Object oldValue = this.message;
			this.message = message;
			pcs.firePropertyChange(MESSAGE_PROPERTY, oldValue, message);
		}
	}

	/**
	 * Returns whether the task should cause the task manager to be displayed in a modal manner. This would be used to cause the user to have
	 * to wait for a task to complete before allowing the user to use the GUI again. Set it to false if the task can be performed in the
	 * background.
	 * 
	 * @return <code>true</code> if the task is modal, <code>false</code> otherwise.
	 */
	public boolean isModal() {
		return modal;
	}

	/**
	 * Sets whether the task is modal or not.
	 * 
	 * @param modal
	 *          <code>true</code> if the task is modal, <code>false</code> otherwise.
	 */
	public void setModal(boolean modal) {
		if (this.modal != modal) {
			boolean oldValue = this.modal;
			this.modal = modal;
			pcs.firePropertyChange(MODAL_PROPERTY, oldValue, modal);
		}
	}

	/**
	 * Gets the current progress of this task. This is a value between 0 and 100.
	 * 
	 * @return the current progress of this task.
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * Sets the current progress of this task.
	 * 
	 * @param progress
	 *          the completion percent of this task.
	 */
	public void setProgress(int progress) {
		if (this.progress != progress) {
			int oldValue = this.progress;
			this.progress = progress;
			pcs.firePropertyChange(PROGRESS_PROPERTY, oldValue, progress);
		}
	}

	/**
	 * Executes this task in a separate thread.
	 */
	public void execute() {
		synchronized (this) {
			if (threadVar == null) {
				threadVar = new ThreadVar(new Thread(this));
				threadVar.get().start();

				notifyAll();
			}
		}
	}

	/**
	 * Waits for this task to complete.
	 * 
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException {
		synchronized (this) {
			while (threadVar == null || !threadVar.get().isAlive()) {
				wait();
			}
		}
		threadVar.get().join();
	}

	/**
	 * Cancels this task.
	 */
	public void cancel() {
		synchronized (this) {
			if (threadVar != null) {
				threadVar.get().interrupt();
			}
		}
	}

	/**
	 * The following code will be executed in a separate thread, this is the code that conforms the task.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

	/**
	 * Class to maintain a reference to the current worker thread under separate synchronization control.
	 */
	private static class ThreadVar {

		private Thread thread;

		ThreadVar(Thread thread) {
			this.thread = thread;
		}

		synchronized Thread get() {
			return thread;
		}

	}

}
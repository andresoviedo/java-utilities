package org.andresoviedo.util.data;

/**
 * A class implementing a synchronized queue.
 * 

 */
public class SynchronizedQueue extends Queue {

	private Object lock = new Object();

	private boolean waiting;

	/**
	 * Constructs a new synchronized queue.
	 */
	public SynchronizedQueue() {
		super();
	}

	/**
	 * Constructs a new synchronized queue with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *          the initial capacity of the queue.
	 */
	public SynchronizedQueue(int initialCapacity) {
		super(initialCapacity);
	}

	/*
	 * @see java.util.Vector#addElement(java.lang.Object)
	 */
	public void addElement(Object obj) {
		synchronized (lock) {
			super.addElement(obj);
			if (waiting) {
				lock.notify();
			}
		}
	}

	public Object get() {
		Object message = null;
		try {
			synchronized (lock) {
				while (message == null) {
					if (size() > 0) {
						message = elementAt(0);
						super.removeElementAt(0);
					} else {
						waiting = true;
						lock.wait();
						waiting = false;
					}
				}
			}
		} catch (InterruptedException e) {
		}
		return message;
	}

	/**
	 * Tries to get the first object in the queue. If the queue is empty, waits a maximum number of milliseconds until an object is added
	 * before returning.
	 * 
	 * @param milliseconds
	 *          the maximum time to wait.
	 * @return the first object in the queue.
	 */
	public Object get(long milliseconds) {
		Object message = null;
		try {
			synchronized (lock) {
				if (size() > 0) {
					message = elementAt(0);
					super.removeElementAt(0);
				} else {
					waiting = true;
					lock.wait(milliseconds);
					waiting = false;
				}
			}
		} catch (InterruptedException e) {
		}
		return message;
	}

	/*
	 * @see java.util.Vector#isEmpty()
	 */
	public boolean isEmpty() {
		synchronized (lock) {
			return (size() == 0);
		}
	}

}
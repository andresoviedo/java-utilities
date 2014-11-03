package org.andresoviedo.util.data;

import java.util.Vector;

/**
 * A generic FIFO (First In - First Out) queue.
 * 

 */
public class Queue extends Vector {

	/**
	 * Constructs a new queue.
	 */
	public Queue() {
		super();
	}

	/**
	 * Constructs a new queue with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *          the initial capacity of the queue.
	 */
	public Queue(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Returns the first object in the queue and removes it.
	 * 
	 * @return the first object in the queue.
	 * @exception EmptyQueueException
	 *              if the queue is empty.
	 */
	public synchronized Object get() {
		if (size() == 0) {
			throw new EmptyQueueException("The queue is empty.");
		}
		return remove(0);
	}

	/**
	 * Returns the first object in the queue without removing it.
	 * 
	 * @return the first object in the queue.
	 * @exception EmptyQueueException
	 *              if the queue is empty.
	 */
	public synchronized Object peek() {
		if (size() == 0) {
			throw new EmptyQueueException("The queue is empty.");
		}
		return get(0);
	}

}
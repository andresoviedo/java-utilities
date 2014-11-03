package org.andresoviedo.util.timer;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class SimpleConstantFlowDispatcher<T> extends ObjectDispatcher<T> implements Runnable {

	private ObjectReceiver<SimpleConstantFlowDispatcher<T>, T> _receiver = null;

	private long _deliveryPeriod = 0; // By default there is no restriction

	private boolean _started = false;

	private Vector<T> _queue = new Vector<T>();

	private long _lastOut = 0; // Keeps track of the last moment when an object
	// was dispatched

	private Thread _dispatchingThread = null;

	public SimpleConstantFlowDispatcher(ObjectReceiver<SimpleConstantFlowDispatcher<T>, T> receiver) {
		_receiver = receiver;
	}

	public void setDeliveryPeriod(long ms) {
		_deliveryPeriod = ms;
	}

	public long getDeliveryPeriod() {
		return _deliveryPeriod;
	}

	@Override
	public boolean start() {
		_started = true;
		if (_dispatchingThread == null) {
			_dispatchingThread = new Thread(this);
			_dispatchingThread.start();
			return true;
		}
		return false;
	}

	@Override
	public boolean stop() {
		_started = false;
		if (_dispatchingThread != null) {
			_dispatchingThread.interrupt();
			try {
				_dispatchingThread.join();
			} catch (InterruptedException inex) {
			}
			_dispatchingThread = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean isStarted() {
		return _started;
	}

	@Override
	public Vector<T> getBufferedObjects() {
		return _queue;
	}

	@Override
	public int internalBufferSize() {
		synchronized (_queue) {
			return _queue.size();
		}
	}

	/**
	 * ATTENTION !! It is dangerous to return an int... Only method for adding objects
	 * 
	 * @param obj
	 *          Object
	 * @return int
	 */
	@Override
	public int addObject(T obj) {
		synchronized (_queue) {
			_queue.add(obj);
			_queue.notify();
			return _queue.indexOf(obj);
		}
	}

	@Override
	public void cleanBuffer() {
		synchronized (_queue) {
			_queue.removeAllElements();
		}
	}

	@Override
	public boolean removeObject(T obj) {
		synchronized (_queue) {
			return _queue.remove(obj);
		}
	}

	public long getTimeUntilNextDispatch() {
		long soonerDelivery = _lastOut + _deliveryPeriod;
		long now = System.currentTimeMillis();
		if (soonerDelivery <= now) {
			// No problem to deliver now so return 0 as waiting time
			return 0;
		} else {
			return (soonerDelivery - now);
		}
	}

	private void dispatch() {
		T dispatchingObject = null;
		synchronized (_queue) {
			if (getTimeUntilNextDispatch() <= 0) {
				try {
					dispatchingObject = _queue.remove(0);
					_lastOut = System.currentTimeMillis();
				} catch (NoSuchElementException nsex) {
				} catch (ArrayIndexOutOfBoundsException aiex) {
				}
			}
		}
		// VERY IMPORTANT! The dispatch is made out of the synchronized block to
		// avoid
		// potential deadlocks because of external synchronization
		if (dispatchingObject != null) {
			_receiver.receiveNextObject(this, dispatchingObject);
		}
	}

	/**
	 * Dispatching method
	 */
	public void run() {
		while (_started) {
			long waitTime = getTimeUntilNextDispatch();
			if (waitTime > 0) {
				// This could be interrupted
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException inex) {
				}
			} else if ((waitTime == 0) && _queue.isEmpty()) {
				try {
					synchronized (_queue) {
						_queue.wait();
					}
				} catch (InterruptedException inex) {
				}
			}
			dispatch();
		}
	}

}

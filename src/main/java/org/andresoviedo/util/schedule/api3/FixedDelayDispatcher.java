package org.andresoviedo.util.schedule.api3;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Clase de proposito general que se comporta como una "linea de retardo". Acepta un argumento Object a traves de la llamada al metodo
 * #addObject y se lo entrega a un ObjectReceiver (interface) llamando al metodo #receiveObject aplicando un retardo de N segundos
 * constante: - La frecuencia de entrada de objetos es igual a la frecuencia de salida de objetos. - Admite un unico ObjectReceiver. -
 * Admite cambiar/consultar el tiempo de retardo. - Start / stop arranca para la entrega de objetos. - En estado stop el contador de tiempos
 * no aplica. - Trabaja con un unico Thread interno.
 * 
 * Uso: FixedDelayDispatcher dispatcher = new FixedDelayDispatcher(listener, 1000); // Retardo fijo de 1 segundo dispatcher.start();
 * 
 * <p>
 * Title: Aplicaciones
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author andresoviedo
 * @version 1.0
 */
public class FixedDelayDispatcher<T> extends ObjectDispatcher<T> implements Runnable {

	private FixedDelayDispatcher<T> _this;

	private Vector<Date> _dates = new Vector<Date>();

	private Vector<T> _objects = new Vector<T>();

	private boolean _started = false;

	private boolean _startAutomatically = false;

	private Thread _thread;

	private long _timeOut;

	private ObjectReceiver<FixedDelayDispatcher<T>, T> _listener;

	public FixedDelayDispatcher(ObjectReceiver<FixedDelayDispatcher<T>, T> listener, long timeOut) {
		_listener = listener;
		_timeOut = timeOut;
		_this = this;
	}

	public void setTimeOut(long timeOut) {
		_timeOut = timeOut;
	}

	public void setStartAutomatically(boolean doStart) {
		_startAutomatically = doStart;
	}

	@Override
	public int addObject(T obj) {
		int ret = -1;
		Date nextJob = new Date(System.currentTimeMillis() + _timeOut);
		synchronized (_objects) {
			ret = _objects.size();
			_dates.add(nextJob);
			_objects.add(obj);
		}
		if (_startAutomatically) {
			start();
		}
		return ret;
	}

	@Override
	public boolean removeObject(T obj) {
		boolean ret = false;
		synchronized (_objects) {
			int index = _objects.indexOf(obj);
			if (index != -1) {
				_dates.removeElementAt(index);
				_objects.removeElementAt(index);
			}
		}
		return ret;
	}

	@Override
	public boolean start() {
		boolean ret = false;
		if (!_started && !_dates.isEmpty()) {
			_thread = new Thread(this);
			_started = true;
			_thread.start();
		}
		return ret;
	}

	@Override
	public boolean stop() {
		boolean ret = false;
		if (_started) {
			_started = false;
			_thread.interrupt();
			try {
				_thread.join();
			} catch (InterruptedException ex) {
			}
		}
		return ret;
	}

	@Override
	public boolean isStarted() {
		return _started;
	}

	@Override
	public int internalBufferSize() {
		int ret;
		synchronized (_objects) {
			ret = _objects.size();
		}
		return ret;
	}

	@Override
	public List<T> getBufferedObjects() {
		return _objects;
	}

	@Override
	public void cleanBuffer() {
		synchronized (_objects) {
			_objects.clear();
			_dates.clear();
		}
	}

	public long getTimeOut() {
		return _timeOut;
	}

	public void run() {
		try {
			recalculateDates();
			while (_started) {
				long sleepTime = 0;
				T nextObject = null;
				synchronized (_objects) {
					if (!_objects.isEmpty()) {
						Date now = new Date(System.currentTimeMillis());
						Date nextJob = _dates.firstElement();
						if ((now.compareTo(nextJob) >= 0)) {
							nextObject = _objects.remove(0);
							_dates.remove(nextJob);
						} else {
							sleepTime = nextJob.getTime() - now.getTime();
						}
					} else {
						break;
					}
				}

				if (nextObject != null) {
					_listener.receiveNextObject(_this, nextObject);
				}

				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
			}
		} catch (InterruptedException ex) {
		}
		_started = false;
	}

	private void recalculateDates() {
		long now = System.currentTimeMillis();
		synchronized (_objects) {
			for (Iterator<Date> iter = _dates.iterator(); iter.hasNext();) {
				Date item = iter.next();
				item.setTime(now + _timeOut);
			}
		}
	}
}

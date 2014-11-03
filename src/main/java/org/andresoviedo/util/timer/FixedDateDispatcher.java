package org.andresoviedo.util.timer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Clase de proposito general que se comporta como una "linea de retardo". Acepta un argumento Object (obj) y un long (delay) a traves de la
 * llamada al metodo #addObject y se lo entrega a un ObjectReceiver (interface) llamando al metodo #receiveObject aplicando un retardo de
 * delay segundos: - La frecuencia de entrada de objetos es igual a la frecuencia de salida de objetos. - Admite un unico ObjectReceiver. -
 * Start / stop arranca para la entrega de objetos. - En estado stop el contador de tiempos no aplica. - Trabaja con un unico Thread
 * interno.
 * 
 * Uso:
 * 
 * VariantDelayDispatcher dispatcher = new VariantDelayDispatcher(listener); Object obj1,obj2,obj3; dispatcher.addObject(obj1,1000);
 * dispatcher.addObject(obj2,5000); dispatcher.addObject(obj3,3000);
 * 
 * El orden de entrega será obj1 (a 1 segundo), obj3 (a 3 segundos), obj2 (a 5 segundos).
 * 
 * @author aoviedo
 * @version 1.0
 */
public class FixedDateDispatcher<T> extends ObjectDispatcher<T> implements Runnable {

	private FixedDateDispatcher<T> _this;

	private List<ObjectDate<T>> _objects = new Vector<ObjectDate<T>>();
	private boolean _started = false;
	private boolean _startAutomatically = false;
	private Thread _thread;
	private ObjectReceiver<FixedDateDispatcher<T>, T> _listener;
	private ObjectDateAscComparator<T> _dateAscComp = new ObjectDateAscComparator<T>();

	public FixedDateDispatcher(ObjectReceiver<FixedDateDispatcher<T>, T> listener) {
		_listener = listener;
		_this = this;
	}

	public void setStartAutomatically(boolean doStart) {
		_startAutomatically = doStart;
	}

	@Override
	public int addObject(T obj) {
		throw new UnsupportedOperationException("Use addObject(Object,Date) instead");
	}

	public int addObject(T obj, Date d) {
		int ret = -1;
		synchronized (_objects) {
			ret = _objects.size();
			_objects.add(new ObjectDate<T>(obj, d));
		}
		if (isStarted()) {
			stop();
			start();
		}
		if (_startAutomatically) {
			start();
		}
		return ret;
	}

	@Override
	public boolean removeObject(Object obj) {
		boolean ret = false;
		synchronized (_objects) {
			for (int i = 0; i < _objects.size(); i++) {
				if ((_objects.get(i))._obj.equals(obj)) {
					_objects.remove(i);
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean start() {
		boolean ret = false;
		if (!_started && !_objects.isEmpty()) {
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
		List<T> ret = new Vector<T>();
		synchronized (_objects) {
			for (int i = 0; i < _objects.size(); i++) {
				ret.add(_objects.get(i)._obj);
			}
		}
		return ret;
	}

	@Override
	public void cleanBuffer() {
		synchronized (_objects) {
			_objects.clear();
		}
	}

	public void run() {
		try {
			while (_started) {
				long sleepTime = 0;
				T nextObject = null;
				synchronized (_objects) {
					if (!_objects.isEmpty()) {
						Date now = new Date(System.currentTimeMillis());
						Collections.sort(_objects, _dateAscComp);
						Date nextJob = (_objects.get(0))._d;
						if ((now.compareTo(nextJob) >= 0)) {
							nextObject = _objects.remove(0)._obj;
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
}

class ObjectDate<T> {
	T _obj;
	Date _d;

	ObjectDate(T obj, Date d) {
		_obj = obj;
		_d = d;
	}

	@Override
	public boolean equals(Object o) {
		return _obj.equals(o);
	}
}

class ObjectDateAscComparator<T> implements Comparator<ObjectDate<T>> {
	@Override
	public int compare(ObjectDate<T> o1, ObjectDate<T> o2) {
		return o1._d.compareTo(o2._d);
	}
}

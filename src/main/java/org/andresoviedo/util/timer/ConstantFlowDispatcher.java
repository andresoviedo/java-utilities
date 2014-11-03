package org.andresoviedo.util.timer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Clase de proposito general que se comporta como un "limitador de flujo". Acepta un argumento Object a traves de la llamada al metodo
 * #addObject y se lo entrega a un ObjectReceiver (interface) llamando al metodo #receiveObject aplicando una frecuencia de entrega
 * constante: - La frecuencia de salida de objetos esta limitada por los metodos: #setTime, #getTime - El tiempo de espera puede aplicarse
 * al principio o al final del bucle de entrega (aplica unicamente a la entrega del primer objeto). Por defecto aplica el retardo al final.
 * - Admite un unico ObjectReceiver. - Start / stop arranca para la entrega de objetos. - Trabaja con un unico Thread interno.
 * 
 * <p>
 * Title: Aplicaciones
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @version 1.0
 */

public class ConstantFlowDispatcher<T> extends ObjectDispatcher<T> implements Runnable {

	private ConstantFlowDispatcher<T> _this;

	private Vector<T> _objects = new Vector<T>();

	private Hashtable<T, ObjectDate> _objectsMetaData = new Hashtable<T, ObjectDate>();

	private Thread _thread;

	private boolean _started = false;

	private ObjectReceiver<ConstantFlowDispatcher<T>, T> _listener;

	private long _time;

	private boolean _waitOnStart = false;

	private long _waitOnStartDelay;

	private long _maxWorkingTime = -1;

	private boolean _startAutomatically = false;

	private int _maxObjectsToDeliver = 1;

	private long _nextDeliverAt = -1;

	private long _maxDeliverAt = -1;

	public ConstantFlowDispatcher(ObjectReceiver<ConstantFlowDispatcher<T>, T> listener, long time) {
		_listener = listener;
		_time = time;
		_waitOnStartDelay = time;
		_this = this;
	}

	public void setWaitOnStart(boolean waitOnStart) {
		_waitOnStart = waitOnStart;
	}

	public void setWaitOnStartDelay(long delay) {
		_waitOnStartDelay = delay;
	}

	public void setStartAutomatically(boolean doStart) {
		_startAutomatically = doStart;
	}

	public void setMaxObjectsToDeliver(int maximum) {
		_maxObjectsToDeliver = maximum;
	}

	public void setMaxWorkingTime(long time) {
		_maxWorkingTime = time;
	}

	@Override
	public int addObject(T obj) {
		int ret = -1;
		synchronized (_objects) {
			ret = _objects.size();
			addObjectAt(obj, ret);
		}
		return ret;
	}

	public void addObjectAtBeginning(T obj) {
		addObjectAt(obj, 0);
	}

	public int addObjectAt(T obj, int index) {
		synchronized (_objects) {
			if (index < 0) {
				throw new IllegalArgumentException("Index must be greater or equal to 0. " + index + "<0");
			} else if (index > _objects.size()) {
				throw new IllegalArgumentException("Index must be lower or equal to size. " + index + ">" + _objects.size());
			} else {
				_objects.insertElementAt(obj, index);
			}
		}
		if (_startAutomatically) {
			start();
		}
		return index;
	}

	/**
	 * Agrega un objecto al dispatcher tal que su tiempo de entrega sea preferiblemente <code>preferredDelay</code> milisegundos después del
	 * siguiente objeto a entregar. <br>
	 * Si el tiempo total que tomará entregar los objetos que hay actualmente en el buffer no supera o no es igual al tiempo especificado como
	 * preferido, <br>
	 * el objeto se entrega al final de los que hay actualmente en el buffer
	 * 
	 * @param obj
	 *          Objeto para agregar
	 * @param preferredDelay
	 *          Tiempo preferido de entrega en milisegundos
	 * @return Siempre devuelve true
	 * @throws IllegalStateException
	 *           si el tiempo especificado es menor que 0
	 */
	public int addObject(T obj, long preferredDelay) {
		if (preferredDelay < 0) {
			throw new IllegalArgumentException("Delay must be greater or equal to 0. " + preferredDelay + "<0");
		}

		synchronized (_objects) {
			ObjectDate dateObject = new ObjectDate(System.currentTimeMillis() + preferredDelay);

			if (_started) {
				// If there is an object expecting to be delivered, then, calculate
				// sleeped time
				long sleepedTime = 0;
				long now = System.currentTimeMillis();
				if (_nextDeliverAt > now) {
					sleepedTime = _time - (_nextDeliverAt - now);
				}
				preferredDelay = preferredDelay + sleepedTime;
			} else {
				// If dispatcher is configured for not waiting on delivering objects
				// at start time, then preferredDelay must be _timed up, because first
				// object is not taken in mind.
				if (!_waitOnStart) {
					preferredDelay = preferredDelay + _time;
				}
			}

			// Normalize delay: preferredDelay must be multiple of _time
			// If delay isn't multiple of _time, then make it multiple then round up
			// (+)
			long modul = preferredDelay % _time;
			if (modul > 0) {
				preferredDelay = preferredDelay + (_time - modul);
			}

			// Translate preferredDelay to object index in Objects Vector.
			int preferredIndex = (int) (preferredDelay / _time) - 1;

			// Por defecto, el elemento se entrega de último.
			int index = _objects.size();

			// If index is lower or egual to max Vector size, then add this
			// object at that index, else,
			if (preferredIndex >= 0 && preferredIndex <= _objects.size()) {
				index = preferredIndex;
			} else {
				index = _objects.size();
			}
			addObjectAt(obj, index);
			_objectsMetaData.put(obj, dateObject);
			return index;
		}
	}

	@Override
	public boolean removeObject(T obj) {
		boolean ret = false;
		synchronized (_objects) {
			ret = _objects.remove(obj);
			_objectsMetaData.remove(obj);
		}
		return ret;
	}

	public void removeAllObjects() {
		cleanBuffer();
	}

	public Vector<T> getObjects() {
		return _objects;
	}

	public T getFirstObject() {
		T ret = null;
		synchronized (_objects) {
			if (!_objects.isEmpty()) {
				ret = _objects.elementAt(0);
			}
		}
		return ret;

	}

	public void setTime(long time) {
		_time = time;
	}

	public long getTime() {
		return _time;
	}

	public void run() {
		while (_started && !_objects.isEmpty()) {
			try {
				if (_waitOnStart) {
					if (!_objects.isEmpty()) {
						_nextDeliverAt = System.currentTimeMillis() + _waitOnStartDelay;
						Thread.sleep(_waitOnStartDelay);
					} else {
						break;
					}
				}

				if (_maxDeliverAt > 0 && System.currentTimeMillis() > _maxDeliverAt) {
					break;
				}

				Vector<T> objectsToDeliver = new Vector<T>();
				if (_started) {
					int objectsToDeliverCounter = _maxObjectsToDeliver;

					synchronized (_objects) {
						long now = System.currentTimeMillis();
						if (!_objects.isEmpty()) {
							for (Iterator<T> it = _objects.iterator(); it.hasNext() && objectsToDeliverCounter > 0;) {
								T obj = it.next();
								ObjectDate objDate = _objectsMetaData.get(obj);
								if (objDate != null) {
									if (now >= objDate._time) {
										objectsToDeliver.add(obj);
										objectsToDeliverCounter--;
										_objectsMetaData.remove(obj);
										it.remove();
									}
								} else {
									objectsToDeliver.add(obj);
									objectsToDeliverCounter--;
									it.remove();
								}
							}
						}
					}
				}

				// entregar el objeto al final para evitar deadlocks. Ejemplo:
				// thread que entrega objecto y otro que agrega. Ambos lucharian
				// por el lock de _objects
				if (!objectsToDeliver.isEmpty()) {
					for (Enumeration<T> e = objectsToDeliver.elements(); e.hasMoreElements();) {
						T obj = e.nextElement();
						if (obj != null) {
							_listener.receiveNextObject(_this, obj);
						}
					}
				}

				// Thread se duerme al final, si y sólo si algun objeto ha sido
				// entregado
				if (!_waitOnStart) {
					if (!objectsToDeliver.isEmpty() || !_objects.isEmpty()) {
						_nextDeliverAt = System.currentTimeMillis() + _time;
						Thread.sleep(_time);
					} else {
						break;
					}
				}
			} catch (InterruptedException ie) {
			}
		}
		_started = false;
	}

	@Override
	public synchronized boolean start() {
		boolean ret = false;
		if (!_started) {
			_started = true;
			_thread = new Thread(this);
			_thread.start();
			ret = true;
		}
		return ret;
	}

	public void reset() {
		if (_maxWorkingTime > 0) {
			_maxDeliverAt = System.currentTimeMillis() + _maxWorkingTime;
			if (_waitOnStart) {
				_maxDeliverAt += _waitOnStartDelay;
			}
		}
	}

	@Override
	public synchronized boolean stop() {
		boolean ret = false;
		if (_started) {
			_started = false;
			_thread.interrupt();
			try {
				_thread.join();
			} catch (InterruptedException iex) {
			}
			ret = true;
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
			_objectsMetaData.clear();
		}
	}

	class ObjectDate {
		long _time;

		ObjectDate(long time) {
			_time = time;
		}
	}
}

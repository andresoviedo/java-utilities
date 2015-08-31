package org.andresoviedo.util.schedule.api3;

import java.util.List;

/**
 * Clase abstracta, implementa mecanismos generales de entrega de objetos: a frecuencia constante, con un retardo constante,...
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

public abstract class ObjectDispatcher<T> {
	public abstract int addObject(T obj);

	public abstract boolean removeObject(T obj);

	public abstract boolean start();

	public abstract boolean stop();

	public abstract boolean isStarted();

	// Control the state of the internal buffer if a stop has been performed
	public abstract int internalBufferSize();

	public abstract List<T> getBufferedObjects();

	public abstract void cleanBuffer();
}

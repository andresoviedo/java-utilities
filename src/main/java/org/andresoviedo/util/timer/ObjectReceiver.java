package org.andresoviedo.util.timer;

public interface ObjectReceiver<D extends ObjectDispatcher<T>, T> {
	public void receiveNextObject(D od, T o);
}

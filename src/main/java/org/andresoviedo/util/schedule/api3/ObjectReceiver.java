package org.andresoviedo.util.schedule.api3;

public interface ObjectReceiver<D extends ObjectDispatcher<T>, T> {
	public void receiveNextObject(D od, T o);
}

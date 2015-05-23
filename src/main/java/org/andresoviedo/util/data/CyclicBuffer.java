package org.andresoviedo.util.data;

public class CyclicBuffer {

	Object[] buffer;
	int maxSize, head = 0, tail = 0, size = 0;

	public CyclicBuffer(int size) {
		this.maxSize = size;
		buffer = new Object[size];
	}

	public void add(Object obj) {
		if (size > 0) {
			tail = ++tail % maxSize;
			if (tail == head) {
				head = ++head % maxSize;
			}
		}
		buffer[tail] = obj;
		if (size < maxSize) {
			size++;
		}
	}

	public int size() {
		return size;
	}

	public Object[] get() {
		int size = size();
		Object[] ret = new Object[size];
		int j = 0;
		for (int i = head; i <= tail; i++) {
			ret[j++] = buffer[i];
		}
		for (int i = tail + 1; i < size; i++) {
			ret[j++] = buffer[i];
		}
		for (int i = 0; i < head; i++) {
			ret[j++] = buffer[i];
		}
		return ret;
	}

	public static void main(String[] args) {
		CyclicBuffer buffer = new CyclicBuffer(15);
		for (int i = 0; i < 30; i++) {
			buffer.add(String.valueOf(i));
			Object[] buf = buffer.get();
			for (int j = 0; j < buf.length; j++) {
				System.out.print("[" + buf[j] + "]");
			}
			System.out.print("--> size[" + buffer.size() + "]\n");
		}
	}
}
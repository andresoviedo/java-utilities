package org.andresoviedo.util.log4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class MemoryAppender extends AppenderSkeleton {

	private List<String> messages = new ArrayList<String>();

	public MemoryAppender() {
		super();
	}

	protected void append(LoggingEvent event) {
		synchronized (messages) {
			messages.add(event.getRenderedMessage());
		}
	}

	public void close() {
		clear();
	}

	public boolean requiresLayout() {
		return false;
	}

	public String[] getMessages() {
		synchronized (messages) {
			return messages.toArray(new String[messages.size()]);
		}
	}

	public void clear() {
		synchronized (messages) {
			messages.clear();
		}
	}

	@Override
	public String toString() {
		return "MemoryAppender [messages=" + messages + "]";
	}

}

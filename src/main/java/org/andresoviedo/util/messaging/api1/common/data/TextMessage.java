package org.andresoviedo.util.messaging.api1.common.data;

import java.io.Serializable;

/**
 * A message holding a text as its body.
 * 
 * @author andresoviedo
 */
public class TextMessage extends ObjectMessage {

	private static final long serialVersionUID = -2521738735986923328L;

	/**
	 * Creates a new <code>TextMessage</code> with no associated text.
	 */
	TextMessage() {
	}

	/**
	 * Creates a new <code>TextMessage</code> with the associated text.
	 * 
	 * @param text
	 *            the associated text.
	 */
	TextMessage(String text) {
		super(text);
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.common.data.ObjectMessage#setObject(java.io.Serializable)
	 */
	public void setObject(Serializable object) {
		if (!(object instanceof String)) {
			throw new IllegalArgumentException("Object is not a string.");
		}
		super.setObject(object);
	}

	/**
	 * Returns the associated text.
	 * 
	 * @return the associated text.
	 */
	public String getText() {
		return (String) getObject();
	}

	/**
	 * Sets the associated text (<code>null</code> permitted).
	 * 
	 * @param text
	 *            the new associated text.
	 */
	public void setText(String text) {
		setObject(text);
	}

}
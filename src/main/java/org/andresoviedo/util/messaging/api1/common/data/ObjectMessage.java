package org.andresoviedo.util.messaging.api1.common.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * A message holding a serializable object as its body.
 * 
 * @author andres
 */
public class ObjectMessage extends Message {

	private static final long serialVersionUID = 2121041691352655240L;

	/**
	 * The associated object. This object is marked as transient because it's serialized in a custom manner.
	 */
	private transient Serializable object;

	/**
	 * Creates a new <code>ObjectMessage</code> with no associated object.
	 */
	ObjectMessage() {
	}

	/**
	 * Creates a new <code>ObjectMessage</code> with no associated object.
	 * 
	 * @param object
	 *          the associated serializable object.
	 */
	ObjectMessage(Serializable object) {
		setObject(object);
	}

	/**
	 * Returns the associated object (may be <code>null</code>).
	 * 
	 * @return the associated object.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Sets the associated object (<code>null</code> permitted).
	 * 
	 * @param object
	 *          the new associated object.
	 */
	public void setObject(Serializable object) {
		this.object = object;
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.common.data.Message#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append(" object[").append(object).append("]");

		return sb.toString();
	}

	/**
	 * Custom implementation of the method to take into account the <code>compressed</code> flag.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		if (isCompressed()) {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream defOut = new ObjectOutputStream(new DeflaterOutputStream(byteOut));
			defOut.writeObject(object);
			defOut.close();
			out.writeInt(byteOut.size());
			out.write(byteOut.toByteArray());
		} else {
			out.writeObject(object);
		}
	}

	/**
	 * Custom implementation of the method to take into account the <code>compressed</code> flag.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (isCompressed()) {
			byte[] buffer = new byte[in.readInt()];
			in.readFully(buffer);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(buffer);
			ObjectInputStream infIn = new ObjectInputStream(new InflaterInputStream(byteIn));
			this.object = (Serializable) infIn.readObject();
		} else {
			this.object = (Serializable) in.readObject();
		}
	}

}
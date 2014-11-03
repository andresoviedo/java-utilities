package org.andresoviedo.util.cache.persisted.api1;

import java.io.File;
import java.io.Serializable;

import org.andresoviedo.util.serialization.api1.XMLSerializable;

public class PersistedListFactory {

	final public static int TYPE_SERIALIZE = 0;

	final public static int TYPE_XML = 1;

	private PersistedListFactory() {
	}

	@SuppressWarnings("unchecked")
	public static PersistedList<?> getObjectStorage(File targetFile, int type) {
		PersistedList<?> ret = null;
		switch (type) {
		case TYPE_SERIALIZE:
			ret = new SerializablePersistedList(targetFile);
			break;
		case TYPE_XML:
			ret = new XMLPersistedList(targetFile);
			break;
		default:
			throw new IllegalArgumentException("Non valid object storage type!!");
		}
		ret.loadObjects();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> PersistedList<T> getObjectStorage(Class<T> clazz, File targetFile, int type) {
		PersistedList<T> ret = null;
		switch (type) {
		case TYPE_SERIALIZE:
			if (!(Serializable.class.isAssignableFrom(clazz))) {
				throw new IllegalArgumentException(clazz + " is not assignable to " + Serializable.class.toString());
			}
			ret = new SerializablePersistedList<T>(targetFile);
			break;
		case TYPE_XML:
			if (!(XMLSerializable.class.isAssignableFrom(clazz))) {
				throw new IllegalArgumentException(clazz + " is not assignable to " + XMLSerializable.class.toString());
			}
			ret = new XMLPersistedList(targetFile);
			break;
		default:
			throw new IllegalArgumentException("Non valid object storage type!!");
		}
		ret.loadObjects();
		return ret;
	}

	public static <T> PersistedList<T> getObjectStorage(Class<T> clazz, File targetFile) {
		return getObjectStorage(clazz, targetFile, TYPE_SERIALIZE);
	}

	public static <T> PersistedList<T> getObjectStorage(Class<T> clazz, String targetFilePath, int type) {
		return getObjectStorage(clazz, new File(targetFilePath), type);
	}

	public static PersistedList<?> getObjectStorage(File targetFile) {
		return getObjectStorage(targetFile, TYPE_SERIALIZE);
	}

	public static PersistedList<?> getObjectStorage(String targetFilePath, int type) {
		return getObjectStorage(new File(targetFilePath), type);
	}
}

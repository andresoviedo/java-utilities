package org.andresoviedo.util.cache.persisted.api1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializablePersistedList<T> extends PersistedList<T> {

	public SerializablePersistedList(File targetFile) {
		super(targetFile);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T readObject(File targetFile) throws IOException {
		IOException throwException = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		T returnObject = null;
		try {
			fis = new FileInputStream(targetFile);
			bis = new BufferedInputStream(fis);
			ois = new ObjectInputStream(bis);
			returnObject = (T) ois.readObject();
		} catch (FileNotFoundException fnfex) {
			throwException = fnfex;
		} catch (IOException ioex) {
			throwException = ioex;
		} catch (ClassNotFoundException cnfex) {
			// TODO: log this?
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ioex) {
				}
				bis = null;
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioex) {
				}
				fis = null;
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException ioex) {
				}
				ois = null;
			}
		}

		if (throwException != null) {
			throw throwException;
		}

		return returnObject;
	}

	@Override
	public void writeObject(File targetFile, Object targetObject) throws IOException {
		IOException throwException = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(targetFile);

			bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);
			oos.writeObject(targetObject);
			oos.flush();
		} catch (FileNotFoundException fnfex) {
			throwException = fnfex;
		} catch (IOException ioex) {
			throwException = ioex;
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException ioex) {
				}
				oos = null;
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException ioex) {
				}
				bos = null;
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioex) {
				}
				fos = null;
			}
		}

		if (throwException != null) {
			throw throwException;
		}
	}
}

package org.andresoviedo.util.messaging.api1.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.data.Message;


/**
 * The persistence class allows saving and getting messages from disk.
 * 
 * @author andres
 */
public class Persistence {

	/**
	 * A file filter used to retrieve message related to a specific client.
	 */
	private class ClientFileFilter implements FileFilter {

		private String clientId;

		public ClientFileFilter(String clientId) {
			this.clientId = clientId;
		}

		/*
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			return (pathname.isFile() && pathname.getName().startsWith(clientId + "-"));
		}

	}

	/**
	 * Iterator to be able to read messages one by one.
	 */
	private class Itr implements Iterator<Object> {

		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		private int cursor = 0;

		/**
		 * A flag indicating whether files should be deleted after the message is deserialized.
		 */
		private boolean delete;

		/**
		 * The list of files, each one containing a serialized message.
		 */
		private File[] files;

		/**
		 * Constructs a new iterator.
		 * 
		 * @param files
		 *          the list of files.
		 * @param delete
		 *          indicates whether files should be deleted after the message is deserialized.
		 */
		public Itr(File[] files, boolean delete) {
			this.files = files;
			this.delete = delete;
		}

		/*
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return (cursor != files.length);
		}

		/*
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			long now = System.currentTimeMillis();
			try {
				Message message = get(files[cursor++], delete);
				// Add the message if it hasn't expired.
				if ((message.getTimeout() == 0) || ((message.getTimeSent().getTime() + message.getTimeout()) >= now)) {
					return message;
				} else if (!delete(message)) {
					logger.warning("Message with id '" + message.getMessageId() + "' could not be deleted.");
				}
			} catch (Exception e) {
			}
			return null;
		}

		/*
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException("This operation is not supported.");
		}

	}

	/**
	 * A task to delete a message when its timeout is reached.
	 */
	private class MessageDeletionTask extends TimerTask {

		private Message message;

		public MessageDeletionTask(Message message) {
			this.message = message;
		}

		/*
		 * @see java.util.TimerTask#run()
		 */
		public void run() {
			if (!delete(message)) {
				logger.warning("Message with id '" + message.getMessageId() + "' could not be deleted.");
			}
		}

	}

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The directory to read messages from and store messages to.
	 */
	private File directory;

	/**
	 * The timer to use to schedule message deletion tasks.
	 */
	private Timer timer;

	/**
	 * Creates a new persistence object which will use the specified directory to store and retrieve message.
	 * 
	 * @param directory
	 *          the directory to use.
	 */
	public Persistence(File directory) {
		this.directory = directory;
		// Ensure the directory is created.
		if (!this.directory.isDirectory()) {
			this.directory.mkdirs();
		}
		// Create the timer.
		this.timer = new Timer(true);
	}

	/**
	 * Saves the specified message to disk. If the message has already been saved, this method does nothing.
	 * 
	 * @param message
	 *          the message to add.
	 * @throws PersistenceException
	 *           if an error occurs while performing the operation.
	 */
	public synchronized void add(Message message) throws PersistenceException {
		Exception throwable = null;
		// Determine the filename.
		File file = new File(directory, getFilename(message));
		// If the file already exists, we're attempting to add the message twice or a duplicate message has been created.
		if (file.exists()) {
			// throw new PersistenceException("The message has already been added to the persistence mechanism.");
			return;
		}

		// Save the message.
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			oos.writeObject(message);
			oos.flush();
		} catch (FileNotFoundException e) {
			throwable = e;
		} catch (IOException e) {
			throwable = e;
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
				oos = null;
			}
		}

		if (throwable != null) {
			throw new PersistenceException(throwable);
		}

		// No exception caught, so schedule the task to delete the message (if needed).
		if (message.getTimeout() > 0) {
			timer.schedule(new MessageDeletionTask(message), message.getTimeout());
		}
	}

	/**
	 * Deletes the specified file from the persistence mechanism.
	 * 
	 * @param message
	 *          the message to be deleted.
	 * @return <code>true</code> if the message was successfully deleted, <code>false</code> if the message wasn't stored in the persistence
	 *         mechanism or an error occured while performing the operation.
	 */
	public synchronized boolean delete(Message message) {
		return new File(directory, getFilename(message)).delete();
	}

	/**
	 * Deletes the referred message from the persistence mechanism.
	 * 
	 * @param messageId
	 *          the global identifier of the message.
	 * @return <code>true</code> if the message was successfully deleted, <code>false</code> if the message wasn't stored in the persistence
	 *         mechanism or an error occured while performing the operation.
	 */
	public synchronized boolean delete(String persistedMessageId) {
		return new File(directory, persistedMessageId).delete();
	}

	/**
	 * Deletes the referred message from the persistence mechanism.
	 * 
	 * @param clientId
	 * @param messageId
	 * @return <code>true</code> if the message was successfully deleted, <code>false</code> if the message wasn't stored in the persistence
	 *         mechanism or an error occured while performing the operation.
	 */
	public synchronized boolean delete(String clientId, String messageId) {
		return new File(directory, getFilename(clientId, messageId)).delete();
	}

	/**
	 * Returns a list with all saved messages. Removes the files after the objects have been read.
	 * 
	 * @return a list with all saved messages.
	 */
	public synchronized List<Message> get() {
		return get(true);
	}

	/**
	 * Returns a list with all saved messages. Notice that expired messages won't be included.
	 * 
	 * @param delete
	 *          indicates whether files have to be deleted after the objects have been read.
	 * @return a list with all saved messages.
	 */
	public synchronized List<Message> get(boolean delete) {
		List<Message> result = new Vector<Message>();
		Message message = null;
		long now = System.currentTimeMillis();

		File[] files = directory.listFiles();
		// Sort files by name.
		Arrays.sort(files);
		for (int i = 0; i < files.length; i++) {
			try {
				// Notice the file is deleted if successfully read.
				message = get(files[i], delete);
				// Add the message if it hasn't expired.
				if ((message.getTimeout() == 0) || ((message.getTimeSent().getTime() + message.getTimeout()) >= now)) {
					result.add(message);
				}
			} catch (Exception e) {
			}
		}

		return result;
	}

	/**
	 * Gets the message read from the specified file. The file will be automatically deleted after read if <code>delete</code> is
	 * <code>true</code>.
	 * 
	 * @param file
	 *          the file to read the message from.
	 * @param delete
	 *          indicates whether the file has to be deleted after the object has been read from it.
	 * @return the message.
	 * @throws Exception
	 *           if an error occurs while performing the operation.
	 */
	private Message get(File file, boolean delete) throws Exception {
		Message result = null;
		Exception throwable = null;

		// Get the message.
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			result = (Message) ois.readObject();
		} catch (FileNotFoundException e) {
			throwable = e;
		} catch (ClassNotFoundException e) {
			throwable = e;
		} catch (InvalidClassException e) {
			throwable = e;
		} catch (StreamCorruptedException e) {
			throwable = e;
		} catch (OptionalDataException e) {
			throwable = e;
		} catch (IOException e) {
			throwable = e;
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
				ois = null;
			}
		}

		// Delete the file.
		if (delete) {
			file.delete();
		}

		if (throwable != null) {
			throw throwable;
		}
		return result;
	}

	/**
	 * Gets the filename based on message information. The filename is constructed using message's id and client id separated by '.'.
	 * 
	 * @param message
	 *          the message.
	 * @return the filename.
	 */
	public String get(Message message) {
		return getFilename((message.getTargetClientId() != null) ? message.getTargetClientId() : message.getClientId(), message.getMessageId());
	}

	/**
	 * Returns the list of persisted messages for a given client id. Notice that expired messages won't be included.
	 * 
	 * @param clientId
	 *          the client id.
	 * @return the list of persisted messages for the specified client id.
	 */
	public synchronized List<Message> get(String clientId) {
		return get(clientId, true);
	}

	/**
	 * Returns the list of persisted messages for a given client id. Notice that expired messages won't be included.
	 * 
	 * @param clientId
	 *          the client id.
	 * @param delete
	 *          indicates whether files have to be deleted after the objects have been read.
	 * @return the list of persisted messages for the specified client id.
	 */
	public synchronized List<Message> get(String clientId, boolean delete) {
		List<Message> result = new Vector<Message>();
		Message message = null;
		long now = System.currentTimeMillis();

		File[] files = directory.listFiles(new ClientFileFilter(clientId));
		// Sort files by name.
		Arrays.sort(files);
		for (int i = 0; i < files.length; i++) {
			try {
				// Notice the file is deleted if successfully read.
				message = get(files[i], delete);
				// Add the message if it hasn't expired.
				if ((message.getTimeout() == 0) || ((message.getTimeSent().getTime() + message.getTimeout()) >= now)) {
					result.add(message);
				}
			} catch (Exception e) {
				// Do nothing for the moment.
			}
		}

		return result;
	}

	/**
	 * Returns the message with the specified id stored in the underlying persistence mechanism.
	 * 
	 * @param clientId
	 *          the client id.
	 * @param messageId
	 *          the id of the message to be retrieved.
	 * @return the message with the specified id stored in the underlying persistence mechanism, or <code>null</code> if not found.
	 */
	@SuppressWarnings("unused")
	private String get(String clientId, String messageId) {
		StringBuffer sb = new StringBuffer();
		sb.append(clientId);
		sb.append('-');
		sb.append(messageId);
		return sb.toString();
	}

	/**
	 * Returns the message with the specified id stored in the underlying persistence mechanism.
	 * 
	 * @param clientId
	 *          the client id.
	 * @param messageId
	 *          the id of the message to be retrieved.
	 * @param delete
	 *          indicates whether the file have to be deleted after the object have been read.
	 * @return the message with the specified id stored in the underlying persistence mechanism, or <code>null</code> if not found.
	 */
	public synchronized Message get(String clientId, String messageId, boolean delete) throws Exception {
		File messageFile = new File(directory, getFilename(clientId, messageId));
		if (messageFile.exists() && messageFile.isFile()) {
			return get(messageFile, delete);
		} else {
			return null;
		}
	}

	/**
	 * Gets the filename based on message information. The filename is constructed using message's id and client id separated by '.'.
	 * 
	 * @param message
	 *          the message.
	 * @return the filename.
	 */
	public String getFilename(Message message) {
		return getPersistedMessageId(message);
	}

	/**
	 * Returns the message with the specified id stored in the underlying persistence mechanism.
	 * 
	 * @param clientId
	 *          the client id.
	 * @param messageId
	 *          the id of the message to be retrieved.
	 * @return the message with the specified id stored in the underlying persistence mechanism, or <code>null</code> if not found.
	 */
	private String getFilename(String clientId, String messageId) {
		StringBuffer sb = new StringBuffer();
		sb.append(clientId);
		sb.append('-');
		sb.append(messageId);
		return sb.toString();
	}

	/**
	 * Returns the list of persisted messages for a given client id. Notice that expired messages won't be included.
	 * 
	 * @param messageId
	 *          the global message id
	 * @param delete
	 *          indicates whether the file have to be deleted after the object have been read
	 * @return the Message identified by id or <code>null</code> if message doesn't exists or it's not a File.
	 * @throws Exception
	 *           if there is a problem reading the message
	 */
	public synchronized Message getMessage(String messageId, boolean delete) throws Exception {
		File messageFile = new File(directory, messageId);
		if (messageFile.exists() && messageFile.isFile()) {
			return get(messageFile, delete);
		} else {
			return null;
		}
	}

	/**
	 * Returns the global message id of the given message.
	 * 
	 * @param message
	 *          the message.
	 * @return the global message id.
	 */
	public String getPersistedMessageId(Message message) {
		// return getFilename((message.getTargetClientId() != null) ? message.getTargetClientId() : message.getClientId(),
		// message.getMessageId());
		return getFilename(message.getClientId(), message.getMessageId());
	}

	/**
	 * Returns an iterator to iterate through all persisted messages. Notice that expired messages won't be included. Calls to
	 * <code>next()</code> may return <code>null</code> if problems are encountered when deserializing the message or if the message has
	 * expired.
	 * 
	 * @param delete
	 *          indicates whether files have to be deleted after the objects have been read.
	 * @return an iterator to iterate through all persisted messages.
	 * @since 2.0.5
	 */
	public synchronized Iterator<?> iterator(boolean delete) {
		File[] files = directory.listFiles();
		// Sort files by name.
		Arrays.sort(files);
		return new Itr(files, delete);
	}

	/**
	 * Returns an iterator to iterate through persisted messages for a given client id. Notice that expired messages won't be included. Calls
	 * to <code>next()</code> may return <code>null</code> if problems are encountered when deserializing the message or if the message has
	 * expired.
	 * 
	 * @param clientId
	 *          the client id.
	 * @param delete
	 *          indicates whether files have to be deleted after the objects have been read.
	 * @return an iterator to iterate through persisted messages for a given client id.
	 * @since 2.0.5
	 */
	public synchronized Iterator<?> iterator(String clientId, boolean delete) {
		File[] files = directory.listFiles(new ClientFileFilter(clientId));
		// Sort files by name.
		Arrays.sort(files);
		return new Itr(files, delete);
	}

}
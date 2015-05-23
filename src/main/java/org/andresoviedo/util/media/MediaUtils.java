package org.andresoviedo.util.media;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;

public class MediaUtils {

	/**
	 * Returns the duration of a certain clip in seconds with double precision. IMPORTANT: The file must be introduced in url format Example:
	 * "file:G:/directory/file.mpg" Can throw MediaUtilsException in case of internal exeptions derived from JMF
	 * 
	 * @param urlFile
	 * @return
	 * @throws MediaUtilsException
	 */
	public static double getVideoFileDuration(String urlFile) throws MediaUtilsException {
		int maxWait = 5000;
		MediaLocator mediaLocator = new MediaLocator(urlFile);
		Object lock = new Object();
		try {
			Player player = Manager.createPlayer(mediaLocator);
			player.addControllerListener(new MediaUtilSupport(lock));
			// Unrealized
			synchronized (lock) {
				player.realize();
				lock.wait(maxWait);
			}
			double d = (double) -1;
			if (player.getState() == Player.Realized) {
				// Realized
				Time duration = player.getDuration();
				// double d = (double)-1;
				if (!duration.equals(Time.TIME_UNKNOWN)) {
					d = duration.getSeconds();
				}
			}
			// Duration computed or unknown
			// Deallocate resources
			player.deallocate();
			player.close();
			/*
			 * synchronized(lock) { player.deallocate(); lock.wait(maxWait); } player.close();
			 */
			return d;
		} catch (IOException ioex) {
			throw new MediaUtilsException("IOException [" + ioex.getMessage() + "]");
		} catch (InterruptedException inex) {
		} catch (NoPlayerException npex) {
			throw new MediaUtilsException("NoPlayerException [" + npex.getMessage() + "]");
		} catch (Throwable th) {
			throw new MediaUtilsException("Throwable [" + th.getMessage() + "]");
		}
		throw new MediaUtilsException("Could not resolve duration");
	}

	/**
	 * IMPORTANT: Do not use it. NullPointerException in some cases!! Returns the dimension (x,y) of a certain clip. IMPORTANT: The file must
	 * be introduced in url format Example: "file:G:/directory/file.mpg" Can throw MediaUtilsException in case of internal exeptions derived
	 * from JMF
	 * 
	 * @param urlFile
	 * @return
	 * @throws MediaUtilsException
	 */
	public static Dimension getVideoFileDimension(String urlFile) throws MediaUtilsException {
		int maxWait = 5000;
		MediaLocator mediaLocator = new MediaLocator(urlFile);
		Object lock = new Object();
		Dimension d = new Dimension(-1, -1);
		try {
			Player player = Manager.createPlayer(mediaLocator);
			player.addControllerListener(new MediaUtilSupport(lock));
			// Unrealized
			synchronized (lock) {
				player.realize();
				lock.wait(maxWait);
			}
			if (player.getState() == Player.Realized) {
				Component visualComponent = player.getVisualComponent();
				d = visualComponent.getPreferredSize();
			}
			return d;
			// } catch (IOException ioex) {
			// throw new MediaUtilsException("IOException
			// ["+ioex.getMessage()+"]");
		} catch (InterruptedException inex) {
			// } catch (NoPlayerException npex) {
			// throw new MediaUtilsException("NoPlayerException
			// ["+npex.getMessage()+"]");
		} catch (Throwable th) {
			th.printStackTrace();
			throw new MediaUtilsException("Throwable [" + th.getMessage() + "]");
		}
		throw new MediaUtilsException("Could not resolve dimension");
	}

}

class MediaUtilSupport implements ControllerListener {

	private Object _lock = null;

	MediaUtilSupport(Object lock) {
		_lock = lock;
	}

	public void controllerUpdate(ControllerEvent event) {
		// System.out.println("Event ["+event+"]");
		if (event instanceof RealizeCompleteEvent) {
			synchronized (_lock) {
				_lock.notify();
			}
		} else if (event instanceof PrefetchCompleteEvent) {
			synchronized (_lock) {
				_lock.notify();
			}
			// } else if(event instanceof DeallocateEvent) {
			// synchronized(_lock) {
			// _lock.notify();
			// }
		}
	}

}

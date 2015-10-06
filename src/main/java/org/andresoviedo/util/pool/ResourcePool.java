package org.andresoviedo.util.pool;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * This class represents a pool of resources.
 * 
 * @author aoviedo
 * 
 */
public class ResourcePool<T> {

	private static final Logger LOG = Logger.getLogger(ResourcePool.class);
	/**
	 * Timeout constant to get or return users to the pool (it can be hacked from JUnit)
	 */
	long timeout = TimeUnit.SECONDS.toMillis(30);
	/**
	 * Underlying implementation of the pool
	 */
	private final BlockingQueue<T> poolOfResources;

	/**
	 * Creates a pool of resources.
	 * 
	 * @param resourceNames
	 *            comma separated users
	 * @param resources
	 *            comma separated passwords
	 */
	public ResourcePool(Collection<T> resources) {
		if (resources == null || resources.size() == 0) {
			throw new RuntimeException("Empty resources");
		}
		this.poolOfResources = new ArrayBlockingQueue<T>(resources.size(), true, resources);
		LOG.debug("Resource pool created with this list of resources '" + Arrays.asList(resources) + "'");
	}

	/**
	 * Just for testing purposes.
	 * 
	 * @return the timeout constant
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Just for testing purposes.
	 * 
	 * @param timeout
	 *            the new timeout
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the tital size of the pool
	 */
	public int size() {
		return poolOfResources.size();
	}

	/**
	 * @return a resource from the pool
	 * @throws RuntimeException
	 *             if thread is interrupted
	 * @throws TimeoutException
	 *             if operation times out waiting for an available resource
	 */
	public T get() throws TimeoutException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Getting resource from the pool...");
		}
		T resource;
		try {
			resource = poolOfResources.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Thread interrupted while waiting to get a resource");
		}
		if (resource == null) {
			throw new TimeoutException("Couldn't get a resource after waiting '" + timeout + "' millis");
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Returned resource '" + resource + "' from the pool.");
		}
		return resource;
	}

	/**
	 * Returns the resource to the pool
	 * 
	 * @param previouslyGottenResource
	 *            the previously gotten resource
	 * @throws RuntimeException
	 *             if thread is interrupted
	 */
	public void putBack(T previouslyGottenResource) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Putting back resource '" + previouslyGottenResource + "'...");
		}
		try {
			if (!poolOfResources.offer(previouslyGottenResource, timeout, TimeUnit.MILLISECONDS)) {
				// INFO: This should never happen because this is supposed to be a pool.
				throw new RuntimeException("Couldn't return the resource '" + previouslyGottenResource + "' to the pool after waiting '"
						+ timeout + "' millis");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Thread interrupted while waiting to return the resource " + previouslyGottenResource
					+ " to the pool");
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("Resource '" + previouslyGottenResource + "' returned to the pool.");
		}
	}

	@Override
	public String toString() {
		return "ResourcePool [timeout=" + timeout + ", poolOfResources=" + poolOfResources + "]";
	}

}
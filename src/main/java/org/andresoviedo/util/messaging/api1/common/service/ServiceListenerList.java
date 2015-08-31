package org.andresoviedo.util.messaging.api1.common.service;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.andresoviedo.util.messaging.api1.common.data.Message;

/**
 * A service listener list holds a map mapping service ids to listener lists, and offers methods to forward messages of a particular service
 * to listeners registered to it.
 * 
 * @author andresoviedo
 */
public class ServiceListenerList {

	/**
	 * A map mapping service ids (of type <code>String</code>) to service listener lists (of type <code>List</code>).
	 */
	private Map<String, ServiceListener> services;

	/**
	 * A map mapping primary service ids (of type <code>String</code>) to secondary service ids (of type <code>String</code>).
	 */
	private Map<String, Set<String>> secondaryServices;

	/**
	 * Creates a new service listener list.
	 */
	public ServiceListenerList() {
		this.services = new Hashtable<String, ServiceListener>();
		this.secondaryServices = new Hashtable<String, Set<String>>();
	}

	/**
	 * Associates a secondary service id to a primary service id.
	 * 
	 * @param primaryServiceId
	 *            the primary service id.
	 * @param secondaryServiceId
	 *            the secondary service id.
	 * @since 2.0.8
	 */
	public void addSecondaryService(String primaryServiceId, String secondaryServiceId) {
		if (primaryServiceId == null) {
			throw new IllegalArgumentException("Null primary service id.");
		}
		if (secondaryServiceId == null) {
			throw new IllegalArgumentException("Null secondary service id.");
		}
		if (primaryServiceId.equals(secondaryServiceId)) {
			throw new IllegalArgumentException("Cannot associate primary service to itself.");
		}
		Set<String> secondaryServicesIds = secondaryServices.get(primaryServiceId);
		if (secondaryServicesIds == null) {
			secondaryServices.put(primaryServiceId, secondaryServicesIds = new HashSet<String>());
		}
		secondaryServicesIds.add(secondaryServiceId);
	}

	/**
	 * Returns the set containing secondary service ids associated to a given primary service id.
	 * 
	 * @param primaryServiceId
	 *            the primary service id.
	 * @return the set containing secondary service ids associated to a given primary service id.
	 * @since 2.0.8
	 */
	public Set<?> getSecondaryServiceIds(String primaryServiceId) {
		if (primaryServiceId == null) {
			throw new IllegalArgumentException("Null primary service id.");
		}
		return secondaryServices.get(primaryServiceId);
	}

	/**
	 * Sets the listener associated to the specified service id. The previous listener (if any) will be removed.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param l
	 *            the associated service listener.
	 * @throws IllegalArgumentException
	 *             if either <code>serviceId</code> or <code>l</code> are <code>null</code>.
	 */
	public void setServiceListener(String serviceId, ServiceListener l) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Null service id.");
		}
		if (l == null) {
			throw new IllegalArgumentException("Null service listener.");
		}
		services.put(serviceId.toUpperCase(), l);
	}

	/**
	 * Removes the service listener associated with the specified service id. If <code>serviceId</code> is <code>null</code>, this method
	 * does nothing.
	 * 
	 * @param serviceId
	 *            the service id which listener has to be removed.
	 * @throws IllegalArgumentException
	 *             if <code>serviceId</code> is <code>null</code>.
	 */
	public void removeServiceListener(String serviceId) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Null service id.");
		}
		services.remove(serviceId.toUpperCase());
	}

	/**
	 * Returns the service listener associated to the specified id.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @return the service listener associated to the specified id.
	 * @throws IllegalArgumentException
	 *             if <code>serviceId</code> is <code>null</code>.
	 */
	public ServiceListener getServiceListener(String serviceId) {
		if (serviceId == null) {
			throw new IllegalArgumentException("Null service id.");
		}
		return services.get(serviceId.toUpperCase());
	}

	/**
	 * Forwards a message to the listener registered to the specified service.
	 * 
	 * @param message
	 *            the message to forward.
	 * @param serviceId
	 *            the service id.
	 * @return <code>true</code> if a service listener was found with the specified id, <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if either <code>message</code> or <code>serviceId</code> are <code>null</code>.
	 * @throws Exception
	 *             the exception thrown by listener's processMessage(), if any. This exception will be probably an uncaught exception in the
	 *             service.
	 */
	public boolean forwardMessage(Message message, String serviceId) throws Exception {
		if (message == null) {
			throw new IllegalArgumentException("Null message.");
		}
		if (serviceId == null) {
			throw new IllegalArgumentException("Null service id.");
		}
		boolean result;
		ServiceListener l = services.get(serviceId.toUpperCase());
		if (result = (l != null)) {
			l.processMessage(message);
		}
		return result;
	}

}
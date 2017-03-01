package org.andresoviedo.util.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

/**
 * Spring simple cache with TTL.
 * 
 * @author andresoviedo
 */
public final class SpringTTLCache extends ConcurrentMapCache implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringTTLCache.class);
	/**
	 * TTL en millis
	 */
	private final long ttl;

	public SpringTTLCache(String name, int ttlseconds) {
		super(name);
		this.ttl = TimeUnit.SECONDS.toMillis(ttlseconds);
		LOGGER.info("SpringTTLCache configured with a TTL of '{}' millis", ttl);
	}

	@Override
	public ValueWrapper get(Object key) {
		final ValueWrapper valueWrapper = super.get(key);
		if (valueWrapper == null) {
			return null;
		} else if (new Date().after(((TTLValueWrapper) valueWrapper.get()).expirationDate)) {
			// LOGGER.debug("Evicted key {}", key);
			super.evict(key);
			return null;
		} else {
			// LOGGER.debug("Cache hit {}", key);
			return (TTLValueWrapper) valueWrapper.get();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object toStoreValue(Object userValue) {
		return new TTLValueWrapper(userValue, new Date(System.currentTimeMillis() + ttl));
	}

	/**
	 * Value wrapper to hold ttl value
	 * 
	 * @author andresoviedo
	 */
	private static final class TTLValueWrapper implements ValueWrapper {

		/**
		 * User value
		 */
		private final Object value;
		/**
		 * Expiration Date (now + TTL)
		 */
		private final Date expirationDate;

		/**
		 * Create a new TTLValueWrapper instance for exposing the given value.
		 * 
		 * @param value
		 *            the value to expose (may be {@code null})
		 * @param expirationDate
		 *            date d'expration du objet
		 */
		private TTLValueWrapper(Object value, Date expirationDate) {
			this.value = value;
			this.expirationDate = expirationDate;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object get() {
			return value;
		}

	}

}
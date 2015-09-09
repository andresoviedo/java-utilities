package org.andresoviedo.util.properties;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p>
 * Extended {@link java.util.Properties} to support multiple environments and/or cluster configurations in a single
 * file.
 * </p>
 * 
 * <p>
 * In order to configure environment and cluster support, the following properties should be defined (example):
 * </p>
 * 
 * <ul>
 * <li>environment=PRO</li>
 * <li>cluster.name=CLUSTER_1</li>
 * </ul>
 * 
 * <p>
 * And the format of the keys and the order of precedence is as follows:
 * </p>
 * <ol>
 * <li>[PRO#CLUSTER_1].key1=value1</li>
 * <li>[PRO].key1=value1</li>
 * <li>key1=value1</li>
 * </ol>
 * 
 * @author andresoviedo
 * 
 */
public class MultiProperties extends Properties {

	private static final Logger LOG = Logger.getLogger(MultiProperties.class);

	private final String environment;
	private final String cluster;

	private static final long serialVersionUID = 435520205801667489L;

	public MultiProperties(Properties defaults) {
		super(defaults);
		this.environment = (String) defaults.get("environment");
		this.cluster = (String) defaults.get("cluster.name");
		LOG.info("Multiproperties created for environment '" + environment + "' and cluster '" + cluster + "'");
	}

	@Override
	public synchronized Object get(Object key) {
		Object ret = null;
		if (environment != null && cluster != null) {
			ret = super.get("[" + environment + "#" + cluster + "]." + key);
		}
		if (ret == null && environment != null) {
			ret = super.get("[" + environment + "]." + key);
		}
		return ret != null ? ret : super.get(key);
	}

	@Override
	public String getProperty(String key) {
		String ret = null;
		if (environment != null && cluster != null) {
			ret = super.getProperty("[" + environment + "#" + cluster + "]." + key);
		}
		if (ret == null && environment != null) {
			ret = super.getProperty("[" + environment + "]." + key);
		}
		return ret != null ? ret : super.getProperty(key);
	}
}

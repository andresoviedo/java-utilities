package org.andresoviedo.util.net;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * This class contains a method to discover hardware addresses (usually MAC) on the computer the application is running on.
 * 
 */
public class NetUtils {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private NetUtils() {
	}

	/**
	 * Returns a list with the hardware address (usually MAC) of each network interface found on the computer the application is running on.
	 * May return an empty list if no network interfaces are found or I/O errors occur.
	 * 
	 * @return a list of hardware addresses.
	 */
	public static List<String> getHardwareAddresses() {
		List<String> result = new Vector<String>();
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
			if (nis != null) {
				while (nis.hasMoreElements()) {
					byte[] mac = nis.nextElement().getHardwareAddress();
					if ((mac != null) && (mac.length == 6)) {
						result.add(getHardwareAddressAsString(mac));
					}
				}
			}
		} catch (SocketException e) {
		}
		return result;
	}

	private static String getHardwareAddressAsString(byte[] mac) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < 6; i++) {
			if (result.length() > 0) {
				result.append("-");
			}
			int value = (mac[i] < 0) ? 256 + mac[i] : mac[i];
			String valueString = Integer.toHexString(value);
			if (valueString.length() < 2) {
				valueString = "0" + valueString;
			}
			result.append(valueString);
		}

		return result.toString().toUpperCase();
	}

}
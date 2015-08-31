package org.andresoviedo.util.jibx;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase que se usa para referenciar desde un custom formater de Jibx ya que este no permite hacer binding sobre atributos de tipo Number.
 * 
 * @author andresoviedo
 *
 */
public class NumberJibx {
	public static Number deserializeNumber(final String value) {
		if (StringUtils.isNotBlank(value)) {
			BigDecimal big = new BigDecimal(value);
			try {
				int scale = big.scale();
				int precision = big.precision();
				if (scale <= 0) {
					// per si l'scale és negatiu el sumem al calcular els digits.
					// En principi l'scale sempre serà 0 en enters.
					if (precision + scale <= 9) {
						return big.intValueExact();
					} else {
						return big.longValueExact();
					}
				} else {
					if (precision <= 15) {
						return big.doubleValue();
					} else {
						// Si tornant un double perdem precisió retornem un BigDecimal
						return big;
					}
				}
			} catch (Exception e) {
				// Si ens ha generat una excepció tornem un BigDecimal
				return big;
			}
		} else {
			return null;
		}
	}

	public static String serializeNumber(final Number num) {
		if (num != null) {
			return num.toString();
		} else {
			return null;
		}
	}
}

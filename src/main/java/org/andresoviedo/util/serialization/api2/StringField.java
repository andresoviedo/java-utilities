/**
 * 
 */
package org.andresoviedo.util.serialization.api2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation encapsulates serialization metadata.
 * 
 * @author aoviedo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StringField {

	/**
	 * @return record identifier
	 */
	String id() default "";

	/**
	 * @return order within global message (only applies to records, not to fields)
	 */
	int order() default -1;

	int offset() default -1;

	String format() default "";

}

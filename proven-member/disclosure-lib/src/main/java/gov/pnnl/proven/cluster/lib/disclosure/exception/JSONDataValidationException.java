/**
 * 
 */
package gov.pnnl.proven.cluster.lib.disclosure.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author raju332
 *
 */
public class JSONDataValidationException extends Exception {

	private static final long serialVersionUID = 1L;
	static Logger log = LoggerFactory.getLogger(JSONDataValidationException.class);
	
	public JSONDataValidationException(String message) {
		super(message);
	}

	public JSONDataValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  The server was acting as a gateway or proxy and received an invalid response from the upstream server.
 */
@ResponseStatus(value=HttpStatus.BAD_GATEWAY)
public class BadUpstreamClientWorkflowException extends ClientErrorWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public BadUpstreamClientWorkflowException(String message) {
		super(message);
	}
	public BadUpstreamClientWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}
	public BadUpstreamClientWorkflowException(Exception ex) {
		super(ex);
	}


	
}

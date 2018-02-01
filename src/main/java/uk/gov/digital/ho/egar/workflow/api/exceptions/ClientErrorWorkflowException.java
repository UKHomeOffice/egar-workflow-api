/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_GATEWAY)
public abstract class ClientErrorWorkflowException extends WorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param ex 
	 *
	 */
	public ClientErrorWorkflowException(Exception ex) {
		super(ex);
	}

	/**
	 * @param message
	 */
	protected ClientErrorWorkflowException(String message) {
		super(message);
	}

	public ClientErrorWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ClientErrorWorkflowException(ResponseEntity<?> reason) {
		super(formatMessage(reason));
	}
	
}

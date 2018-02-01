/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
public class BadRequestWorkflowException extends WorkflowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public BadRequestWorkflowException() {
	}

	/**
	 * @param message
	 */
	public BadRequestWorkflowException(String message) {
		super(message);
	}

	/**
	 * @param message
	 */
	public BadRequestWorkflowException(String message, Throwable cause) {
		super(message,cause);
	}

}

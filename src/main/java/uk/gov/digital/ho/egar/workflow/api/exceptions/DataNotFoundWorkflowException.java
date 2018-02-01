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
abstract public class DataNotFoundWorkflowException extends WorkflowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DataNotFoundWorkflowException() {
	}

	/**
	 * @param message
	 */
	public DataNotFoundWorkflowException(String message) {
		super(message);
	}


}

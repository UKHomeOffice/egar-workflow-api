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
@ResponseStatus(value=HttpStatus.FORBIDDEN)
public class UnableToPerformWorkflowException extends WorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	protected UnableToPerformWorkflowException() {
	}

	/**
	 * @param message
	 */
	public UnableToPerformWorkflowException(String message) {
		super(message);
	}

	
	
	public UnableToPerformWorkflowException(ResponseEntity<?> reason) {
		super(formatMessage(reason));
	}


	
}

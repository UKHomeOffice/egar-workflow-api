package uk.gov.digital.ho.egar.workflow.api.exceptions;

import org.springframework.http.ResponseEntity;

import uk.gov.digital.ho.egar.shared.util.exceptions.NoCallStackException;

public abstract class WorkflowException extends NoCallStackException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkflowException() {
	}

	public WorkflowException(String message) {
		super(message);
	}

	public WorkflowException(Throwable cause) {
		super(cause);
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	protected static String formatMessage(ResponseEntity<?> reason) {
		return String.format("Recieved %d from remote client", reason.getStatusCodeValue() ) ;
	}
}

package uk.gov.digital.ho.egar.workflow.api.exceptions;

public class ForbiddenFileUploadWorkflowException extends UnableToPerformWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ForbiddenFileUploadWorkflowException(final String message) {
		super(message);
	}
}


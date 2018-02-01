package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

public class SubmissionAlreadyExistsException extends UnableToPerformWorkflowException {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SubmissionAlreadyExistsException(final UUID garUuid)
	{
		super(String.format("Can not submit gar: %s as submission already exists.",garUuid.toString()));
	}

}

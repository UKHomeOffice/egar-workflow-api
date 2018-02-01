package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

public class SubmissionNotFoundWorkflowException extends DataNotFoundWorkflowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SubmissionNotFoundWorkflowException(final UUID garUuid)
	{
		super(String.format("Can not find submission for gar %s", garUuid.toString()));
	}
	
}
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 *
 */
public class GarNotFoundGarClientException extends DataNotFoundWorkflowException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GarNotFoundGarClientException(final UUID garUuid)
	{
		super(String.format("Can not find gar %s", garUuid.toString()));
	}
	
}

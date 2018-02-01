package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class FileNotFoundWorkflowException extends DataNotFoundWorkflowException {
	

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FileNotFoundWorkflowException(final UUID garUuid, final UUID fileUuid)
	{
		super(String.format("Can not find file %s in gar %s", fileUuid.toString(), garUuid.toString()));
	}

	public FileNotFoundWorkflowException(final UUID garUuid)
	{
		super(String.format("Can not find file in gar %s", garUuid.toString()));
	}
	
}

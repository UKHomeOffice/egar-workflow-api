/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class CaptainAlreadyExistsWorkflowException extends UnableToPerformWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CaptainAlreadyExistsWorkflowException(final UUID garId, final UUID personId)
	{
		super(String.format("Can not amend person %s in gar %s, as a captain already exists.", personId.toString(), garId.toString()));
	}

	public CaptainAlreadyExistsWorkflowException(final UUID garId)
	{
		super(String.format("Can not add person in gar %s, as a captain already exists.", garId.toString()));
	}
	
}

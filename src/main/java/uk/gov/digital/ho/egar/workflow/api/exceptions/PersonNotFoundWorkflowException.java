/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class PersonNotFoundWorkflowException extends DataNotFoundWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PersonNotFoundWorkflowException(final UUID garId, final UUID personId)
	{
		super(String.format("Can not find person %s in gar %s", personId.toString(), garId.toString()));
	}

	public PersonNotFoundWorkflowException(final UUID garId)
	{
		super(String.format("Can not find person in gar %s", garId.toString()));
	}
	
	public PersonNotFoundWorkflowException(final String message)
	{
		super(message);
	}
	
}

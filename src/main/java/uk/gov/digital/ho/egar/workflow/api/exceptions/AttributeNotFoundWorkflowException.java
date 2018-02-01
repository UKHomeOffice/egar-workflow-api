/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class AttributeNotFoundWorkflowException extends DataNotFoundWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AttributeNotFoundWorkflowException(final UUID garId)
	{
		super(String.format("Can not find attributes in gar %s", garId.toString()));
	}
	
}

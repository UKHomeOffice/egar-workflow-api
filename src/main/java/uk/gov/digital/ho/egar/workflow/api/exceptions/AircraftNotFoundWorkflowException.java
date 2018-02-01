/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class AircraftNotFoundWorkflowException extends DataNotFoundWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AircraftNotFoundWorkflowException(final UUID garId)
	{
		super(String.format("Can not find aircraft in gar %s", garId.toString()));
	}
	
}

/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class LocationNotFoundWorkflowException extends DataNotFoundWorkflowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public LocationNotFoundWorkflowException(final UUID garId, final UUID locationId)
	{
		super(String.format("Can not find location %s in gar %s", locationId.toString(), garId.toString()));
	}

	public LocationNotFoundWorkflowException(final UUID garId)
	{
		super(String.format("Can not find location in gar %s", garId.toString()));
	}
	
}

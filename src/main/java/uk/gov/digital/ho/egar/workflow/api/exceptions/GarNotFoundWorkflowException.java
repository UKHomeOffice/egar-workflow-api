/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.api.exceptions;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author localuser
 *
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST,reason="Gar Not found.") 
public class GarNotFoundWorkflowException extends DataNotFoundWorkflowException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GarNotFoundWorkflowException(final UUID garId)
	{
		super(String.format("Can not find gar %s", garId.toString()));
	}
	
}

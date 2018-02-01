package uk.gov.digital.ho.egar.workflow.service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeResponse;

import java.util.UUID;

/**
 * The attribute service providing interactions with a general aviation reports attributes.
 *
 */
public interface AttributeService {

	/**
	 * Retrieves the attributes for a gar
	 * @param garId The gar uuid.
	 * @return The attributes
	 * @throws WorkflowException 
	 */
	AttributeResponse retrieveAttributes(final AuthValues authToken, final UUID garId) throws WorkflowException;

	/**
	 * Updates the gar with new attribute values.
	 * @param garId The gar id.
	 * @param attribute The attribute value to update.
	 * @throws WorkflowException 
	 */
	void updateAttribute(final AuthValues authToken, final UUID garId, final Attribute attribute) throws WorkflowException;
}
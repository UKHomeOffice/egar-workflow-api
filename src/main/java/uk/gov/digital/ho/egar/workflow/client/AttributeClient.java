package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeWithIdResponse;

import java.util.UUID;

public interface AttributeClient  extends DataClient<AttributeClient> {
	
    AttributeWithIdResponse createAttribute(final AuthValues authToken, Attribute attribute)throws WorkflowException;

    AttributeWithIdResponse updateAttribute(final AuthValues authToken, UUID atrributeId, Attribute attribute)throws WorkflowException;

    AttributeWithIdResponse retrieveAttribute(final AuthValues authToken, UUID atrributeId)throws WorkflowException;
}
 
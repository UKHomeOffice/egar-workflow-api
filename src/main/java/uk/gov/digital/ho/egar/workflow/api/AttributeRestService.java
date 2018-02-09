package uk.gov.digital.ho.egar.workflow.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeResponse;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * The attribute rest service that exposes access and modification methods for an
 * attribute on an existing general aviation report.
 */
public interface AttributeRestService {

    /**
     * Updates the attribute for an existing GAR and returns
     * a redirection url to retrieve the updated attribute
     * @param garId The gar uuid
     * @param attribute The updated attribute value
     * @param request The http request object
     * @return A void response entity
     * @throws URISyntaxException When a redirection url cannot be created.
     * @throws WorkflowException 
     */
    ResponseEntity<Void> postAttribute(final String authToken, final UUID userUuid, final UUID garId, final Attribute attribute, Errors errors) throws WorkflowException;

    /**
     * Retrieves the attribute for an existing GAR
     * @param garId The gar uuid
     * @return The gar's attribute
     * @throws WorkflowException 
     */
    AttributeResponse getAttribute(final String authToken, final UUID userUuid, final UUID garId) throws WorkflowException;
}

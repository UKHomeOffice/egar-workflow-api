package uk.gov.digital.ho.egar.workflow.service;

import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.DataNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;

/**
 * The gar service providing interactions with a general aviation report.
 *
 */
public interface GarService {

    /**
     * Creates a new gar
     * @return The created gar
     * @throws WorkflowException 
     */
    UUID createGar(final AuthValues authValues) throws  WorkflowException;
    
    /**
     * Retrieves a gar summary for an existing gar
     * @param garId The gar uuid
     * @return the gar summary
     * @throws DataNotFoundWorkflowException 
     */
    GarSummary getGarSummary(final AuthValues authValues,final UUID garId) throws  WorkflowException;

    /**
     * Retrieves a gar skeleton for an existing gar
     * @param garId The gar uuid
     * @return the gar skeleton
     * @throws DataNotFoundWorkflowException 
     */
    GarSkeleton getGar(final AuthValues authValues,final UUID garId) throws WorkflowException;

    /**
     * Retrieves a list of existing gars.
     * @return The gar list
     */
    GarListResponse getAllGars(final AuthValues authValues)throws WorkflowException;
}

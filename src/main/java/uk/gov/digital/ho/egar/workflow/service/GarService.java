package uk.gov.digital.ho.egar.workflow.service;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.DataNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarBulkSummaryResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
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
    GarList getAllGars(final AuthValues authValues)throws WorkflowException;

    /**
     * Bulk retrieves Gars in summary format
     * @param authValues
     * @param garList
     * @return list of Gars summaries
     * @throws WorkflowException 
     */
	GarBulkSummaryResponse getBulkGars(final AuthValues authValues,
							   final List<UUID> garList) throws WorkflowException;
}

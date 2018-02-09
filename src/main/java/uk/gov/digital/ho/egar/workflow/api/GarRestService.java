package uk.gov.digital.ho.egar.workflow.api;

import org.springframework.http.ResponseEntity;

import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarBulkSummaryResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;

import java.util.UUID;

/**
 * The person rest service that exposes access and modification methods for an
 * person or collection of persons on an existing general aviation report.
 */
public interface GarRestService {

    /**
     * Retrieves a list of existing general aviation reports
     * @return The list of gars.
     * @throws WorkflowException 
     */
    GarList getListOfGars(final String authToken, 
    							  final UUID userUuid)throws WorkflowException;

    /**
     * Creates a new general aviation reports
     * @return the response.
     * @throws GarRestService 
     */
    ResponseEntity<Void> createGAR(final String authToken, 
    							   final UUID userUuid) throws WorkflowException;

    /**
     * Retrieves the gar summary for an existing gar
     * @param garId the gar uuid.
     * @return The gar summary
     * @throws GarRestService 
     */
    GarSummary retrieveGarSummary(final String authToken,
    							  final UUID userUuid,
    							  final UUID garId) throws  WorkflowException;

    /**
     * Rerieves an existing gar
     * @param garId The gar uuid.
     * @return The gar.
     * @throws GarRestService 
     */
    GarSkeleton retrieveGAR(final String authToken, 
    						final UUID userUuid,
    						final UUID garId) throws WorkflowException;

    /**
     * Bulk retrieves gar summaries for a list of gars 
     * @param authToken
     * @param uuidOfUser
     * @param garList
     * @return
     * @throws WorkflowException 
     */
	GarBulkSummaryResponse bulkRetrieveGARs(final String authToken, 
											final UUID uuidOfUser, 
											final GarList garList) throws WorkflowException;

}

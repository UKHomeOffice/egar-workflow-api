package uk.gov.digital.ho.egar.workflow.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationListResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationResponse;

/**
 * The location rest service that exposes access and modification methods for an
 * location on an existing general aviation report.
 */
public interface LocationRestService {

	 /**
     * Retrieves a list of location uuids on an existing gar
     * @param garId The gar uuid.
     * @return The location list response.
	 * @throws WorkflowException 
     */
	LocationListResponse retrieveAllLocations(final String authToken, final UUID uuidOfUser, UUID garId)
			throws WorkflowException;

	/**
     * Adds or amends departure location to an existing general aviation report
     * @param garId The gar uuid.
     * @param location The location to add
     * @return The response
     * @throws WorkflowException 
	 * @throws IOException 
	 * @throws SolrServerException 
	 * @throws URISyntaxException 
	 * @throws RestClientException 
     */
	ResponseEntity<Void> updateDepartureLocation(final String authToken, final UUID uuidOfUser, UUID garId, Location location)
			throws WorkflowException, RestClientException, URISyntaxException, SolrServerException, IOException;
	/**
     * Adds or amends arrival Location to an existing general aviation report
     * @param garId The gar uuid.
     * @param location The location to add
     * @return The response
     * @throws WorkflowException 
     */
	ResponseEntity<Void> updateArrivalLocation(final String authToken, final UUID uuidOfUser, UUID garId, Location location)
			throws WorkflowException;
	 /**
     * Retrieves a the location details for a location uuid
     * @param garId The gar uuid.
     * @param locationId the location uuid.
     * @return The location response.
     * @throws WorkflowException 
     */
	LocationResponse retrieveSingleLocation(final String authToken, final UUID uuidOfUser, UUID garId, UUID locationId)
			throws WorkflowException;

	/**
     * Retrieves a the location details for dept location.
     * @param garId The gar uuid.
     * @return The location response.
     * @throws WorkflowException 
     */
	LocationResponse retrieveDeptLocation(final String authToken, final UUID uuidOfUser, UUID garId)
			throws WorkflowException;

	/**
     * Retrieves a the location details for arrv location.
     * @param garId The gar uuid.
     * @return The location response.
     * @throws WorkflowException 
     */
	LocationResponse retrieveArrvLocation(final String authToken, final UUID uuidOfUser, UUID garId)
			throws WorkflowException;

}
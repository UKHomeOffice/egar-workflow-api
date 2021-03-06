package uk.gov.digital.ho.egar.workflow.service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationListResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.web.client.RestClientException;

public interface LocationService {

	LocationListResponse retrieveAllLocations(final AuthValues authToken, UUID garId) throws WorkflowException;

	UUID updateDepartureLocation(final AuthValues authToken, UUID garId, Location location) throws WorkflowException, RestClientException, URISyntaxException, SolrServerException, IOException;

	UUID updateArrivalLocation(final AuthValues authToken, UUID garId, Location location) throws WorkflowException;

	LocationResponse retrieveSingleLocation(final AuthValues authToken, UUID garId, UUID locationId) throws WorkflowException;

	LocationResponse retrieveDepartureLocation(final AuthValues authToken, UUID garId) throws WorkflowException;

	LocationResponse retrieveArrivalLocation(final AuthValues authToken, UUID garId) throws WorkflowException;
}
package uk.gov.digital.ho.egar.workflow.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.web.client.RestClientException;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftResponse;

public interface AircraftService {

	UUID createAircraft(final AuthValues authToken,final UUID garId,final Aircraft aircraft) throws  WorkflowException, RestClientException, URISyntaxException, SolrServerException, IOException;

	AircraftResponse retrieveAircraft(final AuthValues authToken,final UUID garId) throws WorkflowException;

}
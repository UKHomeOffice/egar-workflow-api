package uk.gov.digital.ho.egar.workflow.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftResponse;

public interface AircraftRestService {

	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.api.rest.AircraftService#createAircraft(java.lang.String, uk.gov.digital.ho.egar.workflow.model.rest.Aircraft)
	 */
	ResponseEntity<Void> createAircraft(final String authToken, final UUID userUuid,UUID garId, Aircraft aircraft) throws WorkflowException, RestClientException, URISyntaxException, SolrServerException, IOException;

	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.api.rest.AircraftService#retrieveAircraft(java.lang.String)
	 */
	AircraftResponse retrieveAircraft(final String authToken, final UUID userUuid,UUID garId) throws WorkflowException;


}
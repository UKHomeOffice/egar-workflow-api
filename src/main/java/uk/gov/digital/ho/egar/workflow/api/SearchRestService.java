package uk.gov.digital.ho.egar.workflow.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;

import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;

public interface SearchRestService {

	/**
	 * Search for existing people for User.
	 * @param
	 * @throws IOException 
	 * @throws SolrServerException 
	 * @throws URISyntaxException 
	 */
	PersonUUIDList listOfExistingPeople(final String authToken, final UUID uuidOfUser, final String searchCriteria) throws WorkflowException;

	/**
	 * Search list of existing gars for User.
	 * @param
	 * @throws IOException 
	 * @throws SolrServerException 
	 * @throws URISyntaxException 
	 */
	GarListResponse listOfExistingGars(final String authToken, final UUID uuidOfUser, final String searchCriteria) throws WorkflowException;

}
package uk.gov.digital.ho.egar.workflow.api;

import java.net.URISyntaxException;
import java.util.UUID;

import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonListRequest;

public interface SearchRestService {

	/**
	 * Search for existing people for User.
	 * @param
	 * @throws URISyntaxException 
	 */
	PersonListRequest listOfExistingPeople(final String authToken, final UUID uuidOfUser, final String searchCriteria);

	/**
	 * Search list of existing gars for User.
	 * @param
	 * @throws URISyntaxException 
	 */
	GarList listOfExistingGars(final String authToken, final UUID uuidOfUser, final String searchCriteria);

}
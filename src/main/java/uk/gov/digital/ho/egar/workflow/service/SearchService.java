package uk.gov.digital.ho.egar.workflow.service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonListRequest;

public interface SearchService {
	/**
	 * Search for people previously entered for user
	 * @param authValues
	 * @param searchCriteria
	 * @return
	 */
	PersonListRequest searchPeople(final AuthValues authValues, final String searchCriteria);

	/**
	 * Search for Gars using search criteria
	 * @param authValues
	 * @param searchCriteria
	 * @return
	 */
	GarList searchGars(final AuthValues authValues, final String searchCriteria);
}

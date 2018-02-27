package uk.gov.digital.ho.egar.workflow.service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;

public interface SearchService {
	/**
	 * Search for people previously entered for user
	 * @param authValues Authorisation values to use service
	 * @param searchCriteria Search criteria e.g "REG"
	 * @return A list of matching people uuids
	 * @throws WorkflowException
	 */
	PersonUUIDList searchPeople(final AuthValues authValues, final String searchCriteria) throws WorkflowException;

	/**
	 * Search for Gars using search criteria
	 * @param authValues Authorisation values to use service
	 * @param searchCriteria Search criteria e.g "REG"
	 * @return A list of matching gar uuids
	 * @throws WorkflowException
	 */
	 
	GarListResponse searchGars(final AuthValues authValues, final String searchCriteria) throws WorkflowException;
}


package uk.gov.digital.ho.egar.workflow.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarSearchClient;
import uk.gov.digital.ho.egar.workflow.client.PeopleSearchClient;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;
import uk.gov.digital.ho.egar.workflow.service.SearchService;

@Service
public class SearchBusinessLogic implements SearchService {

	@Autowired
	private PeopleSearchClient personRestClient;

	@Autowired
	private GarSearchClient garSearchClient;

	@Override
	public PersonUUIDList searchPeople(AuthValues authValues, String searchCriteria)
			throws WorkflowException {

		PersonUUIDList response = new PersonUUIDList();
		response.setPersonUuids(personRestClient.findMatchingPeople(authValues, searchCriteria));
		return response;

	}

	@Override
	public GarListResponse searchGars(AuthValues authValues, String searchCriteria)
			throws WorkflowException {
		List<UUID> garUuids = garSearchClient.findMatchingGars(authValues, searchCriteria);
		GarListResponse response = new GarListResponse();

		response.setGarIds(garUuids);

		return response;

	}

}


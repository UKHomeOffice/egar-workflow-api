package uk.gov.digital.ho.egar.workflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.PersonClient;
import uk.gov.digital.ho.egar.workflow.client.dummy.DummyPersonClientImpl;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonListRequest;
import uk.gov.digital.ho.egar.workflow.service.SearchService;

@Service
public class DummySearchService implements SearchService{

	@Autowired
	private PersonClient personClient;
	
	@Autowired
	private GarClient garClient;
	
	@Override
	public PersonListRequest searchPeople(AuthValues authValues, String searchCriteria) {
		
		if (personClient instanceof DummyPersonClientImpl){
			return ((DummyPersonClientImpl)personClient).getPeople(authValues);
		}
	
		return new PersonListRequest();
	}


	@Override
	public GarList searchGars(AuthValues authValues, String searchCriteria) {
		try {
			GarList listOfGars = garClient.getListOfGars(authValues);
			return listOfGars;
		} catch (WorkflowException e) {
			
			return new GarList();
		}
		
	}

}

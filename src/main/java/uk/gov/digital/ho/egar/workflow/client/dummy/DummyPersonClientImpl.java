package uk.gov.digital.ho.egar.workflow.client.dummy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.PersonNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.PersonClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientPerson;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonListRequest;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

import java.util.*;
import java.util.Map.Entry;

/**
 * A dummy person service which provides fake data.
 */
@Component
@Profile({"mock-person"})
public class DummyPersonClientImpl extends DummyClient<PersonClient>
									implements PersonClient,InfoContributor {

    private final Map<DummyKey,ClientPerson> dummyPersonRepo = new HashMap<>();

    @Autowired
    private ConversionService conversionService;

    @Override
    public PersonWithIdResponse createPerson(final AuthValues authValues, Person person) {

        ClientPerson clientPerson = conversionService.convert(person, ClientPerson.class);

        clientPerson.setPersonUuid(UUID.randomUUID());
        clientPerson.setUserUuid(authValues.getUserUuid());
        clientPerson = add(clientPerson);

        return conversionService.convert(clientPerson, PersonWithIdResponse.class);

    }

    @Override
    public PersonWithIdResponse updatePerson(final AuthValues authValues, UUID personId, Person person) {

        ClientPerson clientPerson = conversionService.convert(person, ClientPerson.class);

        clientPerson.setPersonUuid(personId);
        clientPerson.setUserUuid(authValues.getUserUuid());
        add(clientPerson);

        return conversionService.convert(clientPerson, PersonWithIdResponse.class);
    }

    @Override
    public PersonWithIdResponse retrievePerson(final AuthValues authValues, UUID personId) throws WorkflowException {
    	
    	DummyKey key = new DummyKey(personId,authValues.getUserUuid());
    	
        ClientPerson clientResponse = dummyPersonRepo.get(key);
        
        if (clientResponse == null) 
			throw new PersonNotFoundWorkflowException("person not found");

        return conversionService.convert(clientResponse, PersonWithIdResponse.class);
    }
    
    @Override
	public List<PersonWithIdResponse> getBulk(AuthValues authValues, List<UUID> peopleUuids) {

    	List<PersonWithIdResponse> people = new ArrayList<>();
    	for(UUID personUuid: peopleUuids){
    		DummyKey key = new DummyKey(personUuid,authValues.getUserUuid());
            ClientPerson clientResponse = dummyPersonRepo.get(key);
            PersonWithIdResponse person = conversionService.convert(clientResponse, PersonWithIdResponse.class);
      
            people.add(person);
    	}
    	return people;
	}
    
	public PersonListRequest getPeople(AuthValues authValues) {
		
		Iterator<Entry<DummyKey, ClientPerson>> iterator = dummyPersonRepo.entrySet().iterator();
		List<UUID> people =  new ArrayList<>(); 
		while (iterator.hasNext()) {
			Entry<DummyKey, ClientPerson> entry = iterator.next();
			if(entry.getKey().getUserUuid().equals(authValues.getUserUuid())){
				people.add(entry.getKey().getKeyUuid());
			}
		}
		PersonListRequest listOfPeople = new PersonListRequest();
		listOfPeople.setPersonUuids(people);
		
    	return listOfPeople;
	}

    private ClientPerson add(final ClientPerson person) {
        dummyPersonRepo.put(new DummyKey(person.getPersonUuid(),person.getUserUuid()), person);
        return person;
    }


}

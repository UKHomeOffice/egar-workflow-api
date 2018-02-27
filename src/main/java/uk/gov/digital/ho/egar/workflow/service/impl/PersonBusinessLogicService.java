/**
 *
 */
package uk.gov.digital.ho.egar.workflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.CaptainAlreadyExistsWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.PersonNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.PeopleSearchClient;
import uk.gov.digital.ho.egar.workflow.client.PersonClient;
import uk.gov.digital.ho.egar.workflow.client.impl.PersonSearchRestClient;
import uk.gov.digital.ho.egar.workflow.model.rest.PeopleSearchDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonType;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonWithId;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PeopleBulkResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.*;
import uk.gov.digital.ho.egar.workflow.service.PersonService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pulls all the services together into a business process.
 */
@Service
public class PersonBusinessLogicService implements PersonService {

    @Autowired
    private GarClient garClient;

    @Autowired
    private PersonClient personClient;
    
    @Autowired
    private GarChecker behaviourChecker;

	@Autowired
	private PeopleSearchClient personSearchClient;
    
    @Autowired
    private ConversionService conversionService;

    @Override
    public PeopleSkeletonResponse getAllPersons(final AuthValues authValues, UUID garUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);

        PeopleSkeletonResponse response = conversionService.convert(gar, PeopleSkeletonResponse.class);

        return response;
    }

    @Override
    public UUID addNewPerson(final AuthValues authValues, UUID garUuid, PersonWithId person) throws WorkflowException {
        
        GarSkeleton gar = garClient.getGar(authValues, garUuid);
       
        behaviourChecker.checkGarExists(gar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, gar);

        PersonType personType = person.getType();

        if (personType == PersonType.CAPTAIN &&
                gar.getPeople() != null &&
                gar.getPeople().getCaptain() != null) {
            throw new CaptainAlreadyExistsWorkflowException(garUuid);
        }
        
        PersonWithIdResponse personWithId;
        if(person.getPersonId() !=null) {
        	personWithId = personClient.retrievePerson(authValues, person.getPersonId());
        }else {
        	personWithId = personClient.createPerson(authValues, person);
        }

        addPersonToGar(gar, personWithId.getPersonId(), personType);
        garClient.updateGar(authValues, garUuid, gar);
        
        // ADD HOOK TO SEARCH
    	PeopleSearchDetails peopleSearchDetails = PeopleSearchDetails.builder()
    			.forename(personWithId.getPersonDetails().getGivenName())
    			.lastname(personWithId.getPersonDetails().getFamilyName())
    			.personUuid(personWithId.getPersonId())
    			.build();
     	personSearchClient.addPersonToIndex(authValues, peopleSearchDetails);


        return personWithId.getPersonId();
    }

    @Override
    public UUID updatePerson(final AuthValues authValues, UUID garUuid, UUID personId, Person person) throws WorkflowException {
        
        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, gar);

        if (!containsPerson(gar, personId)) throw new PersonNotFoundWorkflowException(garUuid, personId);

        PersonType existingType = findPersonType(gar, personId);
        PersonType updatedType = person.getType();

        if (existingType != PersonType.CAPTAIN &&
                updatedType == PersonType.CAPTAIN &&
                gar.getPeople() != null &&
                gar.getPeople().getCaptain() != null) {
            throw new CaptainAlreadyExistsWorkflowException(garUuid, personId);

        }

        PersonWithIdResponse personWithId = personClient.updatePerson(authValues, personId, person);

        if (existingType != updatedType) {
            removePersonFromGar(gar, personId);
            addPersonToGar(gar, personId, updatedType);
        }

        garClient.updateGar(authValues, garUuid, gar);
        
        // ADD HOOK TO SEARCH
    	PeopleSearchDetails peopleSearchDetails = PeopleSearchDetails.builder()
    			.forename(personWithId.getPersonDetails().getGivenName())
    			.lastname(personWithId.getPersonDetails().getFamilyName())
    			.personUuid(personId)
    			.build();
     	personSearchClient.addPersonToIndex(authValues, peopleSearchDetails);

        return personWithId.getPersonId();
    }


    @Override
    public void deletePerson(final AuthValues authValues, UUID garUuid, UUID personId) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, gar);

        if (!containsPerson(gar, personId))
            throw new PersonNotFoundWorkflowException(garUuid, personId);

        removePersonFromGar(gar, personId);

        garClient.updateGar(authValues, garUuid, gar);

    }

    @Override
    public PersonResponse getPerson(final AuthValues authValues, UUID garUuid, UUID personId) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);

        if (!containsPerson(gar, personId))
            throw new PersonNotFoundWorkflowException(garUuid, personId);

        PersonWithIdResponse personWithId = personClient.retrievePerson(authValues, personId);
        personWithId.setType(findPersonType(gar, personId));
        
        PersonResponse response = new PersonResponse();
        response.setPerson(personWithId);
        response.setGarUuid(garUuid);
        response.setUserUuid(gar.getUserUuid());
        
        return response;
    }
    
    @Override
	public PeopleBulkResponse getBulkPeople(AuthValues authValues, List<UUID> peopleUuids) throws WorkflowException {
    	List<PersonWithIdResponse> peopleList = personClient.getBulk(authValues, peopleUuids);
    	PeopleBulkResponse bulkPeople = new PeopleBulkResponse();
    	bulkPeople.setPeople(peopleList);
    	return bulkPeople;
	}



    private void removePersonFromGar(GarSkeleton gar, UUID personId) {
        PersonType type = findPersonType(gar, personId);
        switch (type) {
            case CREW:
                gar.getPeople().getCrew().remove(personId);
                break;
            case CAPTAIN:
                gar.getPeople().setCaptain(null);
                break;
            case PASSENGER:
                gar.getPeople().getPassengers().remove(personId);
                break;
        }
    }

    private void addPersonToGar(GarSkeleton gar, UUID personId, PersonType type) {

        if (gar.getPeople() == null) {
            gar.setPeople(new PeopleSkeletonDetailsResponse());
        }

        if (gar.getPeople().getPassengers()==null){
            gar.getPeople().setPassengers(new ArrayList<>());
        }

        if (gar.getPeople().getCrew()==null){
            gar.getPeople().setCrew(new ArrayList<>());
        }

        switch (type) {
            case CREW:
                gar.getPeople().getCrew().add(personId);
                break;
            case CAPTAIN:
                gar.getPeople().setCaptain(personId);
                break;
            case PASSENGER:
                gar.getPeople().getPassengers().add(personId);
                break;
        }
    }

    private PersonType findPersonType(GarSkeleton gar, UUID personId) {
        PeopleSkeletonDetailsResponse people = gar.getPeople();
        if (people == null) return null;

        if (people.getCaptain()!=null && people.getCaptain().equals(personId)) {
            return PersonType.CAPTAIN;
        }

        if (people.getCrew()!=null && people.getCrew().contains(personId)) {
            return PersonType.CREW;
        }

        if (people.getPassengers()!=null && people.getPassengers().contains(personId)) {
            return PersonType.PASSENGER;
        }

        return null;
    }


    private boolean containsPerson(GarSkeleton gar, UUID personId) {
        PeopleSkeletonDetailsResponse people = gar.getPeople();
        if (people == null) return false;

        if ((people.getCaptain() != null && people.getCaptain().equals(personId)) ||
                (people.getCrew() != null && people.getCrew().contains(personId)) ||
                (people.getPassengers() != null && people.getPassengers().contains(personId))) {
            return true;
        } else {
            return false;
        }


    }
	
}

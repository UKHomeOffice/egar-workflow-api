package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.PeopleSearchClient;
import uk.gov.digital.ho.egar.workflow.model.rest.PeopleSearchDetails;

@Component
@Profile({"mock-person-search"})
public class DummyPeopleSearchClientImpl implements DataClient <PeopleSearchClient>, PeopleSearchClient{
	
	
	private final Map<DummyKey, PeopleSearchDetails> dummySearchIndexRepo = new HashMap<>();
//	private final Map<UUID, PeopleSearchDetails> dummySearchIndexRepo = new HashMap<>();
	

	@Override
	public void addPersonToIndex(final AuthValues authValues, final PeopleSearchDetails peopleDetails) throws WorkflowException {

		dummySearchIndexRepo.put(new DummyKey(peopleDetails.getPersonUuid(),authValues.getUserUuid()), peopleDetails);
			
	}

	@Override
	public List<UUID> findMatchingPeople(final AuthValues authValues, final String searchString) {
		//List<UUID> personUuids;
		DummyKey[] dummyKeys = dummySearchIndexRepo.keySet().toArray(new DummyKey[dummySearchIndexRepo.keySet().size()]);
		List<DummyKey> keyList = new ArrayList<>(Arrays.asList(dummyKeys));
		
		List<PeopleSearchDetails> persons = keyList.stream()
				.filter(key -> authValues.getUserUuid().equals(key.getUserUuid()))
				.map(key -> dummySearchIndexRepo.get(key))
				.collect(Collectors.toList());
		
		if(searchString != null) {
			return persons.stream()
					.filter(person -> person.getForename().equalsIgnoreCase(searchString) || person.getLastname().equalsIgnoreCase(searchString))
					.map(PeopleSearchDetails::getPersonUuid)
					.collect(Collectors.toList());

		}
		
		return persons.stream()
				.map(PeopleSearchDetails::getPersonUuid)
				.collect(Collectors.toList());
		
		
	}

	@Override
	public Health health() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void contribute(Builder builder) {
		// TODO Auto-generated method stub
		
	}
	


}
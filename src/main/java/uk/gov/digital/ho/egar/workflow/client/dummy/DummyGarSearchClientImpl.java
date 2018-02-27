package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.DetailBuilder;
import uk.gov.digital.ho.egar.workflow.client.GarSearchClient;
import uk.gov.digital.ho.egar.workflow.model.rest.GarSearchDetails;

@Component
@Profile({"mock-gar-search"})
public class DummyGarSearchClientImpl implements DataClient<GarSearchClient>,GarSearchClient {
	private static Log logger = LogFactory.getLog(DummyGarSearchClientImpl.class);
	
	public DummyGarSearchClientImpl(@Autowired DetailBuilder details) {
		detailBuilder = details;
		logger.debug("Initialised dummy gar search");
	}
	
	private DetailBuilder detailBuilder ;
	
	private final Map<DummyKey,GarSearchDetails> dummySearchIndexRepo = new HashMap<>();
	
	@Override
	public void addGarToIndex(AuthValues authValues, GarSearchDetails garDetails, boolean updateLocation, boolean updateAircraft)
			throws WorkflowException{
		logger.debug("adding to dummy gar search index: "+ garDetails.getGarUuid());
		// Set the user for gar details
		garDetails.setUserUuid(authValues.getUserUuid());
		// Add gar details to repo
		dummySearchIndexRepo.put(new DummyKey(garDetails.getGarUuid(),garDetails.getUserUuid()), garDetails);
	      
	}

	@Override
	public List<UUID> findMatchingGars(AuthValues authValues, String searchString)
			throws WorkflowException {

		// Get an array of all the dummkeys and convert to list
		DummyKey[] dummyKeys = dummySearchIndexRepo.keySet().toArray(new DummyKey[dummySearchIndexRepo.keySet().size()]);
		List<DummyKey> dummyKeysList = new ArrayList<DummyKey>(Arrays.asList(dummyKeys));
		
		// Filter for only current users and get corresponding gars
		List<GarSearchDetails> gars = dummyKeysList.stream()
				.filter(key -> authValues.getUserUuid().equals(key.getUserUuid()))
				.map(key -> dummySearchIndexRepo.get(key))
				.collect(Collectors.toList());
		
		// Filter through gars using search parameter and returns a list of gar uuids
		List<UUID> garUuids;
		if(searchString != null) {
			garUuids = gars.stream()
					.filter((gar) -> gar.matchesSearchCriteria(searchString))
//				.filter(gar -> gar.getDateOfDeparture()		.equals(LocalDate.parse(searchString)))
				.map(GarSearchDetails::getGarUuid)
				.collect(Collectors.toList());
		} else {
			garUuids = gars.stream()
					.map(GarSearchDetails::getGarUuid)
					.collect(Collectors.toList());
		}
		return garUuids;
	}

	
	@Override
	public Health health() {
		return Health.up().build();
	}

	@Override
	public void contribute(Builder builder) {
		detailBuilder.withDetail(this,builder);
		}

	

}

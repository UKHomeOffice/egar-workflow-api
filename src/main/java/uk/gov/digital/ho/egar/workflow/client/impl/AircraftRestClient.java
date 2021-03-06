package uk.gov.digital.ho.egar.workflow.client.impl;

import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_BULK;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.AircraftClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAircraft;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftWithId;

@Component
@Profile({"!mock-aircraft"})
public class AircraftRestClient extends RestClient<AircraftClient> implements AircraftClient  {
	
	@Autowired
    private ConversionService conversionService;

	/**
     * slf4j logger.
     */
	private static final Logger logger = LoggerFactory.getLogger(GarRestClient.class);
	
	public AircraftRestClient(@Autowired final WorkflowPropertiesConfig urlConfig, @Autowired final RestTemplate restTemplate) {
		super(urlConfig.getAircraftApiURL(), restTemplate);
	}

	@Override
	public AircraftWithId createAircraft(final AuthValues authValues, Aircraft aircraft) throws WorkflowException  {

		
		if (logger.isInfoEnabled())
			logger.info("Create a new aircraft.");

		ClientAircraft clientAircraft = conversionService.convert(aircraft, ClientAircraft.class);
		ResponseEntity<ClientAircraft> response = doPost(authValues, 
													ROOT_PATH_SEPERATOR ,
													clientAircraft,
													ClientAircraft.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), AircraftWithId.class);

	}

	@Override
	public AircraftWithId updateAircraft(final AuthValues authValues, UUID aircraftUuid, Aircraft aircraft) throws WorkflowException {
		
		if (logger.isInfoEnabled())
			logger.info("Update aircraft for:" + aircraftUuid);

		ClientAircraft clientAircraft = conversionService.convert(aircraft, ClientAircraft.class);
		ResponseEntity<ClientAircraft> response = doPost(authValues, 
													ROOT_PATH_SEPERATOR + aircraftUuid + ROOT_PATH_SEPERATOR,
													clientAircraft,
													ClientAircraft.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), AircraftWithId.class);

	}

	@Override
	public AircraftWithId retrieveAircraft(final AuthValues authValues, UUID aircraftUuid) throws WorkflowException {
		
		if (logger.isInfoEnabled())
			logger.info("Request to retrieve a saved aircraft:" + aircraftUuid );

		ResponseEntity<ClientAircraft> response = doGet(authValues, 
													ROOT_PATH_SEPERATOR + aircraftUuid + ROOT_PATH_SEPERATOR,
													ClientAircraft.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), AircraftWithId.class);
			
	}

	@Override
	public List<AircraftWithId> getBulk(AuthValues authValues, List<UUID> aircraftUuids) throws WorkflowException {
		logger.info("Request to retrieve list of aircrafs.");

		ResponseEntity<ClientAircraft[]> responseArray = doPost(authValues, PATH_BULK, aircraftUuids,ClientAircraft[].class  );
		
		if (!HttpStatus.OK.equals(responseArray.getStatusCode()))
			throw new UnableToPerformWorkflowException(responseArray);
		
		List<ClientAircraft> responseList = Arrays.asList(responseArray.getBody());

		TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ClientAircraft.class));
		TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(AircraftWithId.class));

		// Suppressing warning due to using type descriptor within conversion service
		@SuppressWarnings("unchecked")
		List<AircraftWithId> result = (List<AircraftWithId>) conversionService.convert(responseList,sourceType,targetType);
		return result;
		}
}

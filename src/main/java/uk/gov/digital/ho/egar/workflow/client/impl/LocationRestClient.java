package uk.gov.digital.ho.egar.workflow.client.impl;
import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.BadUpstreamClientWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.LocationClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientLocation;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationWithId;

@Component
@Profile({"!mock-location"})
public class LocationRestClient extends RestClient<LocationClient> implements LocationClient {
	
	
	public LocationRestClient(@Autowired final WorkflowPropertiesConfig urlConfig,
						 	  @Autowired final RestTemplate 	 restTemplate) {
		super(urlConfig.getLocationApiURL(), restTemplate);
	}

	@Autowired
    private ConversionService conversionService;

	/**
     * slf4j logger.
     */
	private static final Logger LOG = LoggerFactory.getLogger(GarRestClient.class);
	


	@Override
	public LocationWithId updateLocation(final AuthValues authToken, UUID locationUuid, Location locationRx) throws WorkflowException {
		
		LOG.info("update a location.");
		
		ResponseEntity<ClientLocation> response = doPost( authToken, 
																ROOT_PATH_SEPERATOR + locationUuid + ROOT_PATH_SEPERATOR, 
																locationRx, 
																ClientLocation.class);
		
		if (!HttpStatus.OK.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		LOG.info("ResponseBody", response);
		
		return conversionService.convert(response.getBody(), LocationWithId.class);
	}

	@Override
	public LocationWithId createLocation(final AuthValues authToken, Location locationRx) throws WorkflowException {
		
		LOG.info("Create a new Location.");
		
		ResponseEntity<ClientLocation> response = doPost(authToken,
														 ROOT_PATH_SEPERATOR, 
														 locationRx, 
														 ClientLocation.class);
		
		if (!HttpStatus.OK.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		if ( response.getBody().getLocationUuid() == null )
		{
			throw new BadUpstreamClientWorkflowException("No Location UUID returned when adding location.");
		}
		
		LOG.info("ResponseBody", response);
		return conversionService.convert(response.getBody(), LocationWithId.class);
	}

	@Override
	public LocationWithId retrieveLocation(final AuthValues authToken, UUID locationUuid) throws WorkflowException {

		if ( locationUuid == null )
		{
			// This is to ensure the correct number of locations are kept in a list
			return null ;
		}
		
		LOG.info("Request to retrieve a location.");
		
		ResponseEntity<ClientLocation> response = doGet(authToken,
														ROOT_PATH_SEPERATOR + locationUuid + ROOT_PATH_SEPERATOR,
														ClientLocation.class);
		
		if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		LOG.info("ResponseBody", response);
		return conversionService.convert(response.getBody(), LocationWithId.class);
	}


}
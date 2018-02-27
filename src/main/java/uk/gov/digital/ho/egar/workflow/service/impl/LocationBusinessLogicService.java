/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.service.impl;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.LocationNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.GarSearchClient;
import uk.gov.digital.ho.egar.workflow.client.LocationClient;
import uk.gov.digital.ho.egar.workflow.model.rest.GarSearchDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.*;
import uk.gov.digital.ho.egar.workflow.service.LocationService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Pulls all the services together into a business process.
 */
@Service
public class LocationBusinessLogicService implements LocationService {

	@Autowired
	private GarClient garClient;

    @Autowired
    private LocationClient locationClient;
    
    @Autowired
    private GarChecker behaviourChecker;

    @Autowired
    private ConversionService conversionService;
    
    @Autowired
    private GarSearchClient garSearchClient;

    @Override
    public LocationListResponse retrieveAllLocations(final AuthValues authValues, UUID garUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);

        LocationListResponse response = conversionService.convert(gar, LocationListResponse.class);

        return response;

    }

    @Override
    public UUID updateDepartureLocation(final AuthValues authValues, UUID garUuid, Location location) throws WorkflowException, RestClientException, URISyntaxException, SolrServerException, IOException {
        
        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, gar);

        initaliseClientGarLocations(gar);

        LocationWithId locationWithId;
        if (gar.getLocationIds().get(0)!=null){
            locationWithId = locationClient.updateLocation(authValues, gar.getLocationIds().get(0), location);
        } else{
            locationWithId = locationClient.createLocation(authValues, location);
            gar.getLocationIds().set(0, locationWithId.getLocationId());

            garClient.updateGar(authValues, gar.getGarUuid(), gar);
        }
        // Add gar details to index
        GarSearchDetails garDetails = GarSearchDetails.builder()
        											  .departureLocation(location)
        											  .garUuid(garUuid)
        											  .build();
        garSearchClient.addGarToIndex(authValues, garDetails, true, false);
        
        return locationWithId.getLocationId();
    }

    @Override
    public UUID updateArrivalLocation(final AuthValues authValues, UUID garUuid, Location location) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, gar);
        
        initaliseClientGarLocations(gar);

        LocationWithId locationWithId;
        int arrivalIndex = gar.getLocationIds().size() -1;
        if (gar.getLocationIds().get(arrivalIndex)!=null){
            locationWithId = locationClient.updateLocation(authValues, gar.getLocationIds().get(arrivalIndex), location);
        } else{
            locationWithId = locationClient.createLocation(authValues, location);
            gar.getLocationIds().set(arrivalIndex, locationWithId.getLocationId());

            garClient.updateGar(authValues, gar.getGarUuid(), gar);
        }

        return locationWithId.getLocationId();
    }

    @Override
    public LocationResponse retrieveSingleLocation(final AuthValues authValues, UUID garUuid, UUID locationId) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);

        if (!gar.getLocationIds().contains(locationId)) throw new LocationNotFoundWorkflowException(garUuid, locationId);

        LocationWithId locationWithId = locationClient.retrieveLocation(authValues, locationId);

        LocationResponse response = new LocationResponse();
        response.setGarUuid(garUuid);
        response.setLocation(locationWithId);
        response.setUserUuid(gar.getUserUuid());

        int legNo = gar.getLocationIds().indexOf(locationId);
        response.getLocation().setLegNo(legNo);
        response.getLocation().setLegCount(gar.getLocationIds().size());

        return response;

    }



    @Override
    public LocationResponse retrieveDepartureLocation(final AuthValues authValues, UUID garUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        behaviourChecker.checkGarExists(gar, garUuid);
        
        UUID deptUuid = gar.getLocationIds().get(0);

        if (deptUuid==null){
            return createEmptyLocationResponse(gar);
        }

        LocationWithId locationWithId = locationClient.retrieveLocation(authValues, deptUuid);

        LocationResponse response = new LocationResponse();
        response.setGarUuid(garUuid);
        response.setLocation(locationWithId);
        response.setUserUuid(gar.getUserUuid());

        int legNo = gar.getLocationIds().indexOf(deptUuid);
        response.getLocation().setLegNo(legNo);
        response.getLocation().setLegCount(gar.getLocationIds().size());

        return response;
    }



    @Override
    public LocationResponse retrieveArrivalLocation(final AuthValues authValues, UUID garUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);

        int numLocations = gar.getLocationIds().size();
        if (numLocations<=1){
            return createEmptyLocationResponse(gar);
        }

        UUID arrUuid = gar.getLocationIds().get(numLocations-1);

        if (arrUuid==null){
            return createEmptyLocationResponse(gar);
        }

        LocationWithId locationWithId = locationClient.retrieveLocation(authValues, arrUuid);

        LocationResponse response = new LocationResponse();
        response.setGarUuid(garUuid);
        response.setLocation(locationWithId);
        response.setUserUuid(gar.getUserUuid());

        int legNo = gar.getLocationIds().indexOf(arrUuid);
        response.getLocation().setLegNo(legNo);
        response.getLocation().setLegCount(gar.getLocationIds().size());

        return response;
    }

    private void initaliseClientGarLocations(GarSkeleton gar) {
        if (gar.getLocationIds()==null){
            gar.setLocationIds(new ArrayList<>(Arrays.asList(null, null)));
        }
        int numLocations = gar.getLocationIds().size();
        switch (numLocations) {
            case 0:
                gar.getLocationIds().add(null);
                gar.getLocationIds().add(null);
                break;
            case 1:
                gar.getLocationIds().add(null);
                break;
        }
    }
    private LocationResponse createEmptyLocationResponse(GarSkeleton gar) {
        LocationResponse response = new LocationResponse();
        response.setUserUuid(gar.getUserUuid());
        response.setGarUuid(gar.getGarUuid());
        return response;
    }
}

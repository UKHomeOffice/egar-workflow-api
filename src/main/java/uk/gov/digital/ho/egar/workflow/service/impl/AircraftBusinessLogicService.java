/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.AircraftNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.AircraftClient;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftWithId;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.service.AircraftService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

/**
 * Pulls all the services together into a business process.
 */
@Service
public class AircraftBusinessLogicService implements AircraftService {

	@Autowired
	private GarClient garClient;

    @Autowired
    private AircraftClient aircraftClient;

    @Autowired
    private GarChecker behaviourChecker;
    
	@Override
	public UUID createAircraft(final AuthValues authValues,final UUID garUuid, final Aircraft aircraft) throws WorkflowException {
		
        GarSkeleton gar = garClient.getGar(authValues,garUuid);
        
        behaviourChecker.checkGarExists(gar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, gar);

		AircraftWithId aircraftWithIdResponse;
		if (gar.getAircraftId()!=null){
            aircraftWithIdResponse = aircraftClient.updateAircraft(authValues, gar.getAircraftId(), aircraft);
        } else{
            aircraftWithIdResponse = aircraftClient.createAircraft(authValues, aircraft);
            gar.setAircraftId(aircraftWithIdResponse.getAircraftUuid());
            garClient.updateGar(authValues,gar.getGarUuid(), gar);
        }

        return aircraftWithIdResponse.getAircraftUuid();
	}

	@Override
	public AircraftResponse retrieveAircraft(final AuthValues authValues,final UUID garUuid) throws WorkflowException {
		
        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        behaviourChecker.checkGarExists(gar, garUuid);
		
        if (gar.getAircraftId()== null) throw new AircraftNotFoundWorkflowException(garUuid);

        AircraftWithId aircraft = aircraftClient.retrieveAircraft(authValues, gar.getAircraftId());

        AircraftResponse response = new AircraftResponse();
        response.setGarUuid(garUuid);
        response.setAircraft(aircraft);
        response.setUserUuid(gar.getUserUuid());

        return response;
	} 
	
	
	
}

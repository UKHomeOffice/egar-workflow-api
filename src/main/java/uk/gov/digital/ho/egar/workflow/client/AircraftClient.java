package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftWithId;

import java.util.UUID;

public interface AircraftClient extends DataClient<AircraftClient> {
	
    AircraftWithId createAircraft(final AuthValues authToken ,Aircraft aircraft) throws WorkflowException;

    AircraftWithId updateAircraft(final AuthValues authToken, UUID aircraftId, Aircraft aircraft) throws WorkflowException;

    AircraftWithId retrieveAircraft(final AuthValues authToken, UUID aircraftId) throws WorkflowException;
}

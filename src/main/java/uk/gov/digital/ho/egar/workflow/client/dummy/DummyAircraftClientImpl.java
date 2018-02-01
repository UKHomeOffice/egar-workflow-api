package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.AircraftClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftWithId;


@Component
@Profile({"mock-aircraft"})
public class DummyAircraftClientImpl extends DummyClient<AircraftClient>
									 implements AircraftClient,InfoContributor {
	
	private static final Logger logger = LoggerFactory.getLogger(DummyAircraftClientImpl.class);

    private final Map<DummyKey,ClientAircraft> dummyAircraftRepo = new HashMap<>();

    @Autowired
    private ConversionService conversionService;

    @Override
    public AircraftWithId createAircraft(final AuthValues authToken, final Aircraft aircraft) throws WorkflowException {
    	
    	logger.info("TESTING");

        ClientAircraft clientAircraft = conversionService.convert(aircraft, ClientAircraft.class);

        clientAircraft.setAircraftUuid(UUID.randomUUID());
        clientAircraft.setUserUuid(authToken.getUserUuid());

        clientAircraft = add(clientAircraft);

        return conversionService.convert(clientAircraft, AircraftWithId.class);
    }

    @Override
    public AircraftWithId updateAircraft(final AuthValues authToken, final UUID aircraftId, final Aircraft aircraft) throws WorkflowException{

        ClientAircraft clientAircraft = conversionService.convert(aircraft, ClientAircraft.class);

        clientAircraft.setAircraftUuid(aircraftId);
        clientAircraft.setUserUuid(authToken.getUserUuid());
        add(clientAircraft);

        return conversionService.convert(clientAircraft, AircraftWithId.class);
    }


	@Override
    public AircraftWithId retrieveAircraft(final AuthValues authToken, final UUID aircraftId) throws WorkflowException{

		DummyKey key = new DummyKey(aircraftId,authToken.getUserUuid());
		
        ClientAircraft clientAircraft = dummyAircraftRepo.get(key);
        return conversionService.convert(clientAircraft, AircraftWithId.class);
	}

    private ClientAircraft add(final ClientAircraft aircraft) {
        dummyAircraftRepo.put(new DummyKey(aircraft.getAircraftUuid(),aircraft.getUserUuid()), aircraft);
        return aircraft;
    }
    


}

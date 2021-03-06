package uk.gov.digital.ho.egar.workflow.client.dummy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGar;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGarList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A dummy gar service which provides fake data.
 */
@Component
@Profile({"mock-gar"})
public class DummyGarClientImpl extends DummyClient<GarClient>
								implements GarClient,InfoContributor {

	private final Map<DummyKey,ClientGar> dummyGarRepo = new HashMap<DummyKey,ClientGar>();

    @Autowired
    private ConversionService conversionService;
    
    @Override
    public GarSkeleton createGar(final AuthValues authValues) {
    	
        ClientGar clientGar = new ClientGar();
        
        
        clientGar.setGarUuid(UUID.randomUUID());
        clientGar.setUserUuid(authValues.getUserUuid());

        clientGar = add(clientGar);

        return conversionService.convert(clientGar, GarSkeleton.class);

    }

    @Override
    public GarSkeleton getGar(final AuthValues authValues,final UUID garId) {

    	DummyKey key = new DummyKey(garId,authValues.getUserUuid());

        ClientGar clientResponse = dummyGarRepo.get(key);

        return conversionService.convert(clientResponse, GarSkeleton.class);
    }

    @Override
    public GarSkeleton updateGar(final AuthValues authValues, final UUID garId, final GarSkeleton gar) {

        ClientGar clientGar = conversionService.convert(gar, ClientGar.class);

        clientGar.setGarUuid(garId);
        clientGar.setUserUuid(authValues.getUserUuid());

        add(clientGar);

        return conversionService.convert(clientGar, GarSkeleton.class);

    }


    @Override
    public boolean containsGar(final AuthValues authValues,final UUID garId)
    {
    	DummyKey key = new DummyKey(garId,authValues.getUserUuid());
    	return dummyGarRepo.containsKey(key);
    }

    @Override
    public GarListResponse getListOfGars(final AuthValues authValues) {
        
    	ClientGarList clientResponse = new ClientGarList();
        
        DummyKey[] keys = dummyGarRepo.keySet().toArray(new DummyKey[dummyGarRepo.keySet().size()]);
        
        List<DummyKey> keyList =  new ArrayList<DummyKey>(Arrays.asList(keys));
        
        List<UUID> garIds = keyList.stream()
        		.filter(key -> authValues.getUserUuid().equals(key.getUserUuid()))
        		.map(DummyKey::getKeyUuid)
        .collect(Collectors.toList());
        
        clientResponse.setGarIds(garIds);

        return conversionService.convert(clientResponse, GarListResponse.class);
    }
    
    @Override
	public List<GarSkeleton> getBulk(AuthValues authValues, List<UUID> garUuids) throws WorkflowException {

    	List<GarSkeleton> gars = new ArrayList<>();
    	for(UUID garUuid: garUuids){
    		DummyKey key = new DummyKey(garUuid,authValues.getUserUuid());
            ClientGar clientResponse = dummyGarRepo.get(key);
            GarSkeleton gar = conversionService.convert(clientResponse, GarSkeleton.class);
      
            gars.add(gar);
    	}
		return gars;
	}

    private ClientGar add(final ClientGar clientGar) {
        dummyGarRepo.put(new DummyKey(clientGar.getGarUuid(),clientGar.getUserUuid()), clientGar);
        return clientGar;
    }

	



}

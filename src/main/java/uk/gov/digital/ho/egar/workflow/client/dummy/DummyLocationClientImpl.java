package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.LocationClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientLocation;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationWithId;

@Component
@Profile({"mock-location"})
public class DummyLocationClientImpl extends DummyClient<LocationClient>
									 implements LocationClient,InfoContributor {

	private final Map<DummyKey,ClientLocation> dummyLocationRepo = new HashMap<>();

    @Autowired
    private ConversionService conversionService;

	@Override
	public LocationWithId updateLocation(final AuthValues authToken, UUID locationUuid, Location location) throws WorkflowException {

        ClientLocation clientLocation = conversionService.convert(location, ClientLocation.class);

        clientLocation.setLocationUuid(locationUuid);
        clientLocation.setUserUuid(authToken.getUserUuid());
        clientLocation = add(clientLocation);

        return conversionService.convert(clientLocation, LocationWithId.class);

    }

	@Override
	public LocationWithId createLocation(final AuthValues authToken, Location location) throws WorkflowException {
		
        ClientLocation clientLocation = conversionService.convert(location, ClientLocation.class);

        clientLocation.setLocationUuid(UUID.randomUUID());
        clientLocation.setUserUuid(authToken.getUserUuid());
        add(clientLocation);

        return conversionService.convert(clientLocation, LocationWithId.class);
    }

	@Override
	public LocationWithId retrieveLocation(final AuthValues authToken, UUID locationId) {
		
		DummyKey key = new DummyKey(locationId,authToken.getUserUuid());
		
        ClientLocation clientLocation = dummyLocationRepo.get(key);

        return conversionService.convert(clientLocation, LocationWithId.class);
    }
	
	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private final Validator validator = factory.getValidator();
	
    private ClientLocation add(final ClientLocation location) throws WorkflowException {
    	
    	
    	Set<ConstraintViolation<ClientLocation>> violations = validator.validate(location);
    	if ( !violations.isEmpty() )
    	{
    		throw new UnableToPerformWorkflowException(buildValidationErrorCauseMessage(violations));
    	}
        dummyLocationRepo.put(new DummyKey(location.getLocationUuid(),location.getUserUuid()), location);
        return location;
    }
    
	private static String buildValidationErrorCauseMessage(Set<ConstraintViolation<ClientLocation>> violations) {
		
		StringBuffer sb = new StringBuffer();
		
		for ( ConstraintViolation<?> error : violations )
		{
			sb.append("Error:");
			sb.append(error.getPropertyPath());
			sb.append(" '");
			sb.append(error.getMessage());
			sb.append("';\r");
		}
		
		return sb.toString();
	}


}

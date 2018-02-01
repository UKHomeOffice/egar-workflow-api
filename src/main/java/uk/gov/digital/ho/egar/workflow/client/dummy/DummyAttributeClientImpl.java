package uk.gov.digital.ho.egar.workflow.client.dummy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.AttributeClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAttribute;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeWithIdResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A dummy attribute service which returns fake data and interactions with a gars attributes.
 */
@Component
@Profile({"mock-attribute"})
public class DummyAttributeClientImpl extends DummyClient<AttributeClient>
 							implements AttributeClient,InfoContributor {

    private final Map<DummyKey,ClientAttribute> dummyAttributeRepo = new HashMap<>();

    @Autowired
    private ConversionService conversionService;

    @Override
    public AttributeWithIdResponse createAttribute(final AuthValues authToken, Attribute attribute)throws WorkflowException {

        ClientAttribute clientAttribute = conversionService.convert(attribute, ClientAttribute.class);

        clientAttribute.setAttributeUuid(UUID.randomUUID());
        clientAttribute.setUserUuid(authToken.getUserUuid());

        clientAttribute = add(clientAttribute);

        return conversionService.convert(clientAttribute, AttributeWithIdResponse.class);
    }

    @Override
    public AttributeWithIdResponse updateAttribute(final AuthValues authToken, UUID atrributeId, Attribute attribute) throws WorkflowException{
        
    	ClientAttribute clientAttribute = conversionService.convert(attribute, ClientAttribute.class);

        clientAttribute.setAttributeUuid(atrributeId);
        clientAttribute.setUserUuid(authToken.getUserUuid());
        add(clientAttribute);

        return conversionService.convert(clientAttribute, AttributeWithIdResponse.class);
    }

    @Override
    public AttributeWithIdResponse retrieveAttribute(final AuthValues authToken, UUID atrributeId)throws WorkflowException {

		DummyKey key = new DummyKey(atrributeId,authToken.getUserUuid());

        ClientAttribute clientAttribute = dummyAttributeRepo.get(key);

        return conversionService.convert(clientAttribute, AttributeWithIdResponse.class);
    }

    private ClientAttribute add(final ClientAttribute attribute) {
        dummyAttributeRepo.put(new DummyKey(attribute.getAttributeUuid(),attribute.getUserUuid()), attribute);
        return attribute;
    }
    

}

/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.AttributeNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.adapter.AttributeToMapConverter;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.service.AttributeService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

import java.util.Map;
import java.util.UUID;

/**
 * Pulls all the services together into a business process.
 */
@Service
public class AttributeBusinessLogicService implements AttributeService {

    protected final Log logger = LogFactory.getLog(getClass());
	
    @Autowired
	private GarClient garClient;
    
    @Autowired
    private GarChecker behaviourChecker;
	
	@Autowired
	private ConversionService conversionService;

	@Autowired
    private AttributeToMapConverter attributeToMapConverter;

    @Override
    public AttributeResponse retrieveAttributes(final AuthValues authValues,UUID garUuid) throws WorkflowException {
        
    	
    	if ( logger.isInfoEnabled() ) logger.info("Retrieve GAR");
    	
        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        behaviourChecker.checkGarExists(gar, garUuid);
		
        if (gar.getAttributes() == false) 
        	throw new AttributeNotFoundWorkflowException(garUuid);

        AttributeResponse response = conversionService.convert(gar, AttributeResponse.class);
        
        return response;
    }

    @Override
    public void updateAttribute(final AuthValues authValues,UUID garUuid, Attribute attribute) throws WorkflowException {
        
        GarSkeleton clientGar = garClient.getGar(authValues, garUuid);
        
        behaviourChecker.checkGarExists(clientGar, garUuid);
		behaviourChecker.checkGarIsAmendable(authValues, clientGar);

        
        Map<String, String> attributeMap = attributeToMapConverter.convert(attribute);

        clientGar.setAttributeMap(attributeMap);

        garClient.updateGar(authValues, garUuid, clientGar);
    }
}

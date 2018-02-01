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
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.AttributeClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAttribute;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeWithIdResponse;

@Component
@Profile({"!mock-attribute"})
public class AttributeRestClient extends RestClient<AttributeClient> implements AttributeClient {

	@Autowired
    private ConversionService conversionService;

	/**
     * slf4j logger.
     */
	private static final Logger logger = LoggerFactory.getLogger(GarRestClient.class);
	

	public AttributeRestClient(@Autowired final WorkflowPropertiesConfig urlConfig, @Autowired final RestTemplate restTemplate) {
		super(urlConfig.getAttributeApiURL(), restTemplate);
	}

	
	@Override
	public AttributeWithIdResponse createAttribute(final AuthValues authValues, Attribute attribute) throws WorkflowException {
		
		if (logger.isInfoEnabled())
			logger.info("Create a new attribute.");

		ClientAttribute clientAttribute = conversionService.convert(attribute, ClientAttribute.class);
		ResponseEntity<ClientAttribute> response = doPost(authValues, 
													ROOT_PATH_SEPERATOR ,
													clientAttribute,
													ClientAttribute.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), AttributeWithIdResponse.class);
	}

	@Override
	public AttributeWithIdResponse updateAttribute(final AuthValues authValues, UUID attributeUuid, Attribute attribute) throws WorkflowException{

		if (logger.isInfoEnabled())
			logger.info("Update attribute for:" + attributeUuid);

		ClientAttribute clientAttribute = conversionService.convert(attribute, ClientAttribute.class);
		ResponseEntity<ClientAttribute> response = doPost(authValues, 
													ROOT_PATH_SEPERATOR + attributeUuid + ROOT_PATH_SEPERATOR,
													clientAttribute,
													ClientAttribute.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), AttributeWithIdResponse.class);
	}

	@Override
	public AttributeWithIdResponse retrieveAttribute(final AuthValues authValues, UUID attributeUuid) throws WorkflowException{

		if (logger.isInfoEnabled())
			logger.info("Request to retrieve a saved attribute:" + attributeUuid );

		ResponseEntity<ClientAttribute> response = doGet(authValues, 
													ROOT_PATH_SEPERATOR + attributeUuid + ROOT_PATH_SEPERATOR,
													ClientAttribute.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), AttributeWithIdResponse.class);
	}

}

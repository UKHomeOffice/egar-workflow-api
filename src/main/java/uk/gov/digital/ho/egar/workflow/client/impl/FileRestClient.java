package uk.gov.digital.ho.egar.workflow.client.impl;

import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_BULK;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.FileClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileDetails;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;

@Component
@Profile({"!mock-file"})
public class FileRestClient  extends RestClient<FileClient> implements FileClient{

	public FileRestClient(@Autowired final WorkflowPropertiesConfig urlConfig,
						  @Autowired final RestTemplate 			restTemplate){
		super( urlConfig.getFileApiURL() , restTemplate) ;
	}
	
	@Autowired
    private ConversionService conversionService;

	/**
     * slf4j logger.
     */
	private static final Logger logger = LoggerFactory.getLogger(GarRestClient.class);
	
	public FileWithIdResponse uploadFileInformation(AuthValues authValues,
													FileInformation fileInfo) throws WorkflowException {

		if ( logger.isInfoEnabled()) logger.info("Upload a file");
		
		ClientFileDetails clientFile = conversionService.convert(fileInfo, ClientFileDetails.class);
		ResponseEntity<ClientFileDetails> response = doPost(authValues,ROOT_PATH_SEPERATOR,clientFile , ClientFileDetails.class);
		
		if (!HttpStatus.OK.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		if ( logger.isInfoEnabled()) logger.info("ResponseBody", response.getBody());
		
		return conversionService.convert(response.getBody(), FileWithIdResponse.class);
	}

	@Override
	public FileWithIdResponse retrieveFileDetails(AuthValues authValues, 
							 		  UUID fileUuid) throws WorkflowException {

		if ( logger.isInfoEnabled()) 
			logger.info("Request to retrieve uploaded file %s for user %s.", fileUuid, authValues.getUserUuid());
		
		String url = ROOT_PATH_SEPERATOR + fileUuid + ROOT_PATH_SEPERATOR;
		
		ResponseEntity<ClientFileDetails> response = doGet(authValues,url,ClientFileDetails.class);
		
		if (!HttpStatus.OK.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		if ( logger.isInfoEnabled()) logger.info("ResponseBody", response.getBody());
		
		return conversionService.convert(response.getBody(), FileWithIdResponse.class);
	}

	@Override
	public List<FileWithIdResponse> getBulk(AuthValues authValues, List<UUID> fileUuids) throws WorkflowException {
		logger.info("Request to retrieve list of people.");

		ResponseEntity<ClientFileDetails[]> responseArray = doPost(authValues, PATH_BULK, fileUuids,ClientFileDetails[].class  );
		
		if (!HttpStatus.OK.equals(responseArray.getStatusCode()))
			throw new UnableToPerformWorkflowException(responseArray);
		
		List<ClientFileDetails> responseList = Arrays.asList(responseArray.getBody());

		TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ClientFileDetails.class));
		TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(FileWithIdResponse.class));

		// Suppressing warning due to using type descriptor within conversion service
		@SuppressWarnings("unchecked")
		List<FileWithIdResponse> result = (List<FileWithIdResponse>) conversionService.convert(responseList,sourceType,targetType);
		return result;
	}
}

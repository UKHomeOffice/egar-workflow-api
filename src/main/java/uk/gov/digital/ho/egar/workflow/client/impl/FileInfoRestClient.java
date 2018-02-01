package uk.gov.digital.ho.egar.workflow.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.FileInfoClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileInformation;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileInformationRequest;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;

import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;

@Component
@Profile({"!mock-file"})
public class FileInfoRestClient extends RestClient<FileInfoClient> implements FileInfoClient {

	public FileInfoRestClient(@Autowired final WorkflowPropertiesConfig urlConfig,
							  @Autowired final RestTemplate 			restTemplate){
		super( urlConfig.getFileinfoApiURL() , restTemplate) ;
	}
	
	@Autowired
    private ConversionService conversionService;

	@Override
	public FileInformation retrieveFileInformation(AuthValues authValues, FileDetails fileDetails) throws WorkflowException {

		ClientFileInformationRequest clientFile = conversionService.convert(fileDetails, ClientFileInformationRequest.class);

		ResponseEntity<ClientFileInformation> response = doPost(authValues,ROOT_PATH_SEPERATOR, clientFile , ClientFileInformation.class);
		
		if (!HttpStatus.OK.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);

		return  conversionService.convert(response.getBody(), FileInformation.class);
	}

}

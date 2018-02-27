package uk.gov.digital.ho.egar.workflow.client.impl;

import java.util.Arrays;
import java.util.List;
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
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.SubmissionNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.SubmissionClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientSubmission;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;
import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_BULK;

@Component
@Profile({"!mock-submission"})
public class SubmissionRestClient extends RestClient<SubmissionClient> implements SubmissionClient {

	/**
	 * slf4j logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(GarRestClient.class);

	@Autowired
	private ConversionService conversionService;

    public SubmissionRestClient(@Autowired	final WorkflowPropertiesConfig urlConfig,
                                @Autowired final RestTemplate 		restTemplate) {
        super(urlConfig.getSubmissionApiURL(), restTemplate);
    }
	
	@Override
	public SubmissionGar submit(final UserValues userValues, final GarSummary summary) throws WorkflowException {
		
		ClientSubmission clientSubmission = conversionService.convert(summary,ClientSubmission.class);
		
		ResponseEntity<SubmissionGar> response = doPost(userValues, ROOT_PATH_SEPERATOR , clientSubmission, SubmissionGar.class );
		
		if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		return response.getBody();
	}

	@Override
	public SubmissionGar getSubmission(AuthValues authValues, UUID submissionUuid) throws WorkflowException {
		
		ResponseEntity<SubmissionGar> response = doGet(authValues,ROOT_PATH_SEPERATOR + submissionUuid + ROOT_PATH_SEPERATOR, SubmissionGar.class);
		
		if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) 
			throw new UnableToPerformWorkflowException (response);
		
		return response.getBody();
	}

	@Override
	public boolean containsSubmission(AuthValues authValues, UUID submissionUuid) throws WorkflowException {
		try {
			return getSubmission( authValues, submissionUuid).getSubmission().getSubmissionUuid().equals(submissionUuid);
		} catch (SubmissionNotFoundWorkflowException e) {
			if ( logger.isInfoEnabled()) logger.info("Submission UUID not found");
			return false;
		}
	}

	@Override
	public SubmissionGar cancel(final UserValues userValues, final UUID submissionUuid) throws WorkflowException {
		ResponseEntity<SubmissionGar> response = doDelete(userValues,ROOT_PATH_SEPERATOR + submissionUuid + ROOT_PATH_SEPERATOR, SubmissionGar.class);

		if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException (response);

		return response.getBody();
	}

	@Override
	public List<SubmissionGar> getBulk(AuthValues authValues, List<UUID> submissionUuids) throws WorkflowException {
		logger.info("Request to retrieve list of people.");

		ResponseEntity<SubmissionGar[]> responseArray = doPost(authValues, PATH_BULK, submissionUuids,SubmissionGar[].class  );
		
		if (!HttpStatus.OK.equals(responseArray.getStatusCode()))
			throw new UnableToPerformWorkflowException(responseArray);
		
		return Arrays.asList(responseArray.getBody());
	}


}

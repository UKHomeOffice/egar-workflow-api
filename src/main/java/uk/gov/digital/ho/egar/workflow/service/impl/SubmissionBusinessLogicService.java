package uk.gov.digital.ho.egar.workflow.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.SubmissionNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.SubmissionClient;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;
import uk.gov.digital.ho.egar.workflow.service.SubmissionService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

/**
 * Pulls all the services together into a business process.
 */
@Service
public class SubmissionBusinessLogicService implements SubmissionService {

	@Autowired
	private SubmissionClient submissionClient;
	
	@Autowired
	private GarClient garClient;
	
	@Autowired
	private GarChecker garChecker;
	
	@Autowired
	private GarBusinessLogicService garBusinessLogicService;
	
	@Override
	public SubmissionGar retrieveSubmission(final AuthValues authValues, final UUID garUuid)
			throws WorkflowException {
				
		
        GarSkeleton gar = garClient.getGar(authValues, garUuid);
        
        garChecker.checkGarExists(gar, garUuid);

        if (gar.getSubmissionId() == null) 
        	throw new SubmissionNotFoundWorkflowException(garUuid);

		return submissionClient.getSubmission(authValues, gar.getSubmissionId());

    }

	@Override
	public UUID submit(final UserValues userValues, final UUID garUuid) throws WorkflowException {
		
        GarSkeleton gar = garClient.getGar(userValues, garUuid);
        
        garChecker.checkGarExists(gar, garUuid);
		garChecker.checkGarIsAmendable(userValues, gar);
		garChecker.checkGarIsSubmittable(userValues, gar);
		
		GarSummary summary = garBusinessLogicService.getGarSummary(userValues, garUuid);
		
		for(FileWithIdResponse file :  summary.getFiles()) { 
		      if(file.getFileStatus() != FileStatus.VIRUS_SCANNED)  
		        throw new UnableToPerformWorkflowException("One or more files have not been virus scanned"); 
		    } 
		
		SubmissionGar submission = submissionClient.submit(userValues, summary);
		
		UUID submissionId =  submission.getSubmission().getSubmissionUuid();
		gar.setSubmissionId(submissionId);
        garClient.updateGar(userValues,gar.getGarUuid(), gar);

		
		return submissionId;
	}

	@Override
	public UUID cancel(final UserValues userValues, final UUID garUuid) throws WorkflowException {
		GarSkeleton gar = garClient.getGar(userValues, garUuid);

		garChecker.checkGarExists(gar, garUuid);

		garChecker.checkGarIsCancellable(userValues, gar);

		SubmissionGar submission = submissionClient.cancel(userValues, gar.getSubmissionId());

		UUID submissionId =  submission.getSubmission().getSubmissionUuid();
		gar.setSubmissionId(submissionId);
		garClient.updateGar(userValues,gar.getGarUuid(), gar);

		return submissionId;
	}
}
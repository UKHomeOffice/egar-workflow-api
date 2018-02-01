package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.GarNotFoundGarClientException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.SubmissionNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.SubmissionClient;
import uk.gov.digital.ho.egar.workflow.model.rest.SubmissionStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.SubmissionType;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionWithId;

@Component 
@Profile("mock-submission")
public class DummySubmissionClient extends DummyClient<SubmissionClient>
								  implements SubmissionClient,InfoContributor {

    private final Map<DummyKey,SubmissionGar> dummySubmissionRepo = new HashMap<>();
    
    /* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.client.dummy.SubmissionClient#getSubmission(uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues, java.util.UUID)
	 */
    @Override
	public SubmissionGar getSubmission(final AuthValues authValues, final UUID submissionUuid) throws SubmissionNotFoundWorkflowException{
    	
    	DummyKey key = new DummyKey(submissionUuid,authValues.getUserUuid());
    	
    	SubmissionGar submission = dummySubmissionRepo.get(key);
    	
    	return submission;
    }

    /* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.client.dummy.SubmissionClient#submit(uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues, java.util.UUID)
	 */
    @Override
	public SubmissionGar submit(final UserValues userValues, final GarSummary summary) throws GarNotFoundGarClientException{
    	
    	
    	SubmissionGar submission = new SubmissionGar(); 
    	SubmissionWithId innerSubmission = new SubmissionWithId();
    	innerSubmission.setSubmissionUuid(UUID.randomUUID());
    	innerSubmission.setType(SubmissionType.CBP_STT);
    	innerSubmission.setExternalRef(UUID.randomUUID().toString());
    	innerSubmission.setStatus(SubmissionStatus.SUBMITTED);
    	submission.setGarUuid(summary.getGarUuid());
    	submission.setUserUuid(userValues.getUserUuid());
    	submission.setSubmission(innerSubmission);
    	
    	submission =add(submission);
    	
    	return submission;
    }
    
    
    @Override
    public boolean containsSubmission(final AuthValues authValues, final UUID submissionUuid)
    {
    	DummyKey key = new DummyKey(submissionUuid, authValues.getUserUuid());
    	return dummySubmissionRepo.containsKey(key);
    }

	@Override
	public SubmissionGar cancel(UserValues userValues, UUID submissionUuid) throws WorkflowException {
		SubmissionGar submission = getSubmission(userValues, submissionUuid);
		submission.getSubmission().setStatus(SubmissionStatus.CANCELLED);
		return submission;
	}

	private SubmissionGar add(final SubmissionGar submission) {
		dummySubmissionRepo.put(new DummyKey(submission.getSubmission().getSubmissionUuid(),submission.getUserUuid()), submission);
		return submission;
	}
	

}

package uk.gov.digital.ho.egar.workflow.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;

/**
* The submission rest service that exposes access and submission 
* to an existing general aviation report.
*/
public interface SubmissionRestService {
	
	 /**
     * Submit a general aviation report
     * @param garUuid The gar uuid.
     */
	ResponseEntity<Void> submit(final String authHeader,
								final UUID uuidOfUser,
								final String forename,
								final String surname,
								final String email,
								final String contact,
								final String altContact, 
					            final UUID garUuid) throws WorkflowException;
	
	/**
     * Get a submitted general aviation report
     * @param garUuid The gar uuid.
     */
	SubmissionGar retrieveSubmission(final String authToken, 
									 final UUID uuidOfUser, 
									 final UUID garUuid) throws WorkflowException;


	/**
	 * Cancels the submission of a general aviation report
	 * @param garUuid the gar uuid.
	 * @return
	 * @throws WorkflowException Is thrown when the operation could not be carried out.
	 */
	ResponseEntity<Void> cancel(final String authHeader,
								final UUID uuidOfUser,
								final String forename,
								final String surname,
								final String email,
								final String contact,
								final String altContact,
								final UUID garUuid) throws WorkflowException;
}
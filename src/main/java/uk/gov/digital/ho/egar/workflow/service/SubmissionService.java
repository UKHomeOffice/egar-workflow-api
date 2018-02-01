package uk.gov.digital.ho.egar.workflow.service;

import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;

/**
 * The Submissions service providing interactions with a general aviation reports.
 *
 */
public interface SubmissionService {

	/**
     * Retrieves an existing gar submission record.
	 * @param userValues The auth values for the user
	 * @param submissionUuid The submission uuid
     * @return The Submission
     * @throws WorkflowException  Is thrown when the operation could not be carried out.
     */
	SubmissionGar retrieveSubmission(final AuthValues authValues,final UUID submissionUuid) throws WorkflowException;

	/**
     * Submits an existing gar
	 * @param userValues The auth values for the use
	 * @param garUuid the gar uuid
     * @return The submission with an id.
     * @throws WorkflowException  Is thrown when the operation could not be carried out.
     */
	UUID submit(final UserValues userValues, final UUID garUuid) throws WorkflowException ;

	/**
	 * Cancels a gar submission
	 * @param userValues The auth values for the user
	 * @param garUuid The gar to cancel
	 * @return The submission uuid
	 * @throws WorkflowException Is thrown when the operation could not be carried out.
	 */
	UUID cancel(UserValues userValues, UUID garUuid) throws WorkflowException;

}

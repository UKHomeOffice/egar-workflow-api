package uk.gov.digital.ho.egar.workflow.client;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;

public interface SubmissionClient  extends DataClient<SubmissionClient> {

	SubmissionGar submit(final UserValues userValues, final GarSummary summary) throws WorkflowException;

	SubmissionGar getSubmission(final AuthValues authValues, final UUID submissionUuid) throws WorkflowException;

	boolean containsSubmission(final AuthValues authValues, final UUID submissionUuid) throws WorkflowException;

    SubmissionGar cancel(final UserValues userValues, final UUID submissionUuid) throws WorkflowException;

	List<SubmissionGar> getBulk(final AuthValues authValues,final  List<UUID> submissionUuids) throws WorkflowException;
}
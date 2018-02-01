package uk.gov.digital.ho.egar.workflow.service.behaviour;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.GarNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.SubmissionAlreadyExistsException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.SubmissionNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.SubmissionClient;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.SubmissionStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;
import uk.gov.digital.ho.egar.workflow.service.LocationService;

@Component
public class GarCheck implements GarChecker {

    /**
     * The submission client.
     */
    @Autowired
    private SubmissionClient submissionClient;

    /**
     * The location service.
     */
    @Autowired
    private LocationService locationService;

    @Autowired
    private WorkflowPropertiesConfig config;

    /**
     * Checks that a gar is amendable.
     * A gar is amendable if it hasn't been submitted at all, or it is not in a submitted or pending status
     *
     * @param authValues The authentication values for a user.
     * @param gar        The skeleton gar.
     * @throws WorkflowException Is thrown when you cannot change the gar.
     */
    @Override
    public void checkGarIsAmendable(final AuthValues authValues, final GarSkeleton gar)
            throws WorkflowException {

        if (gar.getSubmissionId() != null) {
            SubmissionGar submissionGar = submissionClient.getSubmission(authValues, gar.getSubmissionId());
            SubmissionStatus status = submissionGar.getSubmission().getStatus();
            if (status == SubmissionStatus.SUBMITTED || status == SubmissionStatus.PENDING) {
                throw new SubmissionAlreadyExistsException(gar.getGarUuid());
            }
        }
    }

    /**
     * Checks that the gar exists.
     *
     * @param gar   The gar skeleton
     * @param garId The gar id
     * @throws WorkflowException Is thrown when a gar does not exist
     */
    @Override
    public void checkGarExists(final GarSkeleton gar, final UUID garId) throws WorkflowException {
        if (gar == null) throw new GarNotFoundWorkflowException(garId);

    }

    @Override
    public void checkGarIsCancellable(AuthValues userValues, GarSkeleton gar) throws WorkflowException {

        if (!gar.getSubmission()) {
            throw new SubmissionNotFoundWorkflowException(gar.getGarUuid());
        }

        SubmissionGar submissionGar = submissionClient.getSubmission(userValues, gar.getSubmissionId());

        //Checks if submission isn't in a submitted status
        if (!(submissionGar.getSubmission().getStatus() == SubmissionStatus.SUBMITTED)) {
            throw new UnableToPerformWorkflowException(String.format("Submission for gar '%s' is not in a 'submitted' status. Cannot cancel.", gar.getGarUuid()));
        }

        LocationResponse arrival = locationService.retrieveArrivalLocation(userValues, gar.getGarUuid());

        if (arrival != null && arrival.getLocation() != null && arrival.getLocation().getDateTime() != null) {
            ZonedDateTime arrivalDateTime = arrival.getLocation().getDateTime();

            ZonedDateTime now = ZonedDateTime.now();
            if (arrivalDateTime.isBefore(now)) {
                throw new UnableToPerformWorkflowException(String.format("Submitted gar '%s' arrived in the past. Unable to cancel.", gar.getGarUuid()));
            }

            Duration d = Duration.between(ZonedDateTime.now(), arrivalDateTime);

            if (d.toMillis() - config.getCancellationCutoffTimeMs() < 0) {
                throw new UnableToPerformWorkflowException(String.format("Submitted gar '%s' is to close to arrival. Unable to cancel.", gar.getGarUuid()));
            }
        }
    }
}

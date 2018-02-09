package uk.gov.digital.ho.egar.workflow.service.behaviour;

import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;

public interface GarChecker {

	void checkGarIsAmendable(final AuthValues userValues, final GarSkeleton gar) throws WorkflowException ;

	void checkGarExists(final GarSkeleton gar, final UUID garId) throws WorkflowException;

	void checkGarIsCancellable(final AuthValues userValues, final GarSkeleton gar) throws WorkflowException;
	
	void checkGarIsSubmittable(final AuthValues userValues, final GarSkeleton gar) throws WorkflowException;

}

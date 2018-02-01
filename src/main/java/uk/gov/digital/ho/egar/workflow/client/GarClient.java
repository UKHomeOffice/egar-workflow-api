package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;

import java.util.UUID;

public interface GarClient  extends DataClient<GarClient> {
	
    GarSkeleton createGar(final AuthValues authToken)throws WorkflowException;

    GarSkeleton updateGar(final AuthValues authToken, final UUID garId, final GarSkeleton clientGar) throws WorkflowException;

    /**
     * Find a GAR
     * @return null if not found.
     * @throws WorkflowException
     */
    GarSkeleton getGar(final AuthValues authToken, UUID garId) throws WorkflowException;

    @Deprecated
    boolean containsGar(final AuthValues authToken, UUID garId)throws WorkflowException ;

    GarListResponse getListOfGars(final AuthValues authToken)throws WorkflowException;
}

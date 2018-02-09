package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationWithId;

import java.util.List;
import java.util.UUID;

public interface LocationClient  extends DataClient<LocationClient> {

    LocationWithId updateLocation(final AuthValues authToken, UUID locationUuid, Location location)throws WorkflowException;

    LocationWithId createLocation(final AuthValues authToken, Location clientLocation)throws WorkflowException;

    /**
     * @return An object or null if locationId is null.
     * 			This is to ensure the correct number of locations are kept in a list.
     * @throws WorkflowException
     */
    LocationWithId retrieveLocation(final AuthValues authToken, UUID locationId)throws WorkflowException;

	List<LocationWithId> getBulk(final AuthValues authValues, final List<UUID> locationUuids) throws WorkflowException;
}

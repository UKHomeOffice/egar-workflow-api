package uk.gov.digital.ho.egar.workflow.api;

import java.util.UUID;

import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PeopleBulkResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;

public interface BulkPersonRestService {

	 /**
     * Bulk retrieve people for an existing user.
     * @param authToken
     * @param uuidOfUser
     * @param peopleList	list of people uuids
     * @return list of people details
     * @throws WorkflowException
     */
	PeopleBulkResponse bulkRetrievePeople(final String authToken, final UUID uuidOfUser, final PersonUUIDList peopleList) throws WorkflowException;

}
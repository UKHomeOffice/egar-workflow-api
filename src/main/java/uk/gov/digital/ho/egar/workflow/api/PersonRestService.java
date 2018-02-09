package uk.gov.digital.ho.egar.workflow.api;

import org.springframework.http.ResponseEntity;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonWithId;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSkeletonResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonResponse;

import java.util.UUID;

/**
 * The person rest service that exposes access and modification methods for an
 * person or collection of persons on an existing general aviation report.
 */
public interface PersonRestService {

    /**
     * Retrieves a list of person uuids on an existing gar
     * @param garId The gar uuid.
     * @return The person skeleton response.
     * @throws WorkflowException 
     */
    PeopleSkeletonResponse getAllPersons(final String authToken, final UUID uuidOfUser, final UUID garId) throws  WorkflowException;

    /**
     * Adds a new person to an existing general aviation report
     * @param garId The gar uuid.
     * @param person The person to add
     * @return The response
     * @throws WorkflowException when there is an issue performing this step of the process.
     */
    ResponseEntity<Void> addNewPerson(final String authToken, final UUID uuidOfUser, final UUID garId, final PersonWithId person) throws WorkflowException;

    /**
     * Updates the person for an existing general aviation report.
     * @param garId The gar uuid.
     * @param personId The person uuid.
     * @param person The person details to update.
     * @return The response
     * @throws WorkflowException when there is an issue performing this step of the process.
     */
    ResponseEntity<Void> updatePerson(final String authToken, final UUID uuidOfUser, final UUID garId, final UUID personId, final Person person) throws WorkflowException;

    /**
     * Gets an existing person on an existing general aviation report.
     * @param garId the gar uuid.
     * @param personId the person uuid.
     * @return The person
     */
    PersonResponse getPerson(final String authToken, final UUID uuidOfUser, final UUID garId,final UUID personId) throws WorkflowException;

    /**
     * Delete an existing person on an existing general aviation report.
     * @param garId the gar uuid.
     * @param personId The person uuid.
     * @return The response
     * @throws WorkflowException 
     */
    ResponseEntity<Void> deletePerson(final String authToken, final UUID uuidOfUser, final UUID garId, final UUID personId) throws WorkflowException;

}

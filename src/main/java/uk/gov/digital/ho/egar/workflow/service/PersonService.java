package uk.gov.digital.ho.egar.workflow.service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSkeletonResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonResponse;
import java.util.UUID;

/**
 * The person service providing interactions with a general aviation reports people.
 *
 */
public interface PersonService {

    /**
     * Retrieves a hierarchy of person identifiers on a gar.
     * @param garId The gar uuid.
     * @return The people skeleton response
     * @throws WorkflowException 
     */
    PeopleSkeletonResponse getAllPersons(final AuthValues authToken, final UUID garId) throws WorkflowException;

    /**
     * Adds a new person for an existing gar
     * @param garId the gar uuid
     * @param person the person to add
     * @return The person with an id.
     * @throws WorkflowException 
     */
    UUID addNewPerson(final AuthValues authToken, final UUID garId, final Person person) throws WorkflowException;

    /**
     * Updates an existing person on an existing gar
     * @param garId The gar uuid
     * @param personId the person uuid
     * @param person The person object to update.
     * @throws WorkflowException
     */
    UUID updatePerson(final AuthValues authToken, final UUID garId,final UUID personId,final Person person) throws WorkflowException;

    /**
     * Deletes an existing person from an existing gar
     * @param garId The gar uuid
     * @param personId the person uuid
     * @throws WorkflowException
     */
    void deletePerson(final AuthValues authToken, final UUID garId, final UUID personId) throws WorkflowException;

    /**
     * Retrieves an existing person from an existing gar
     * @param garId The gar uuid
     * @param personId the person uuid
     * @return The persons details
     * @throws WorkflowException 
     */
    PersonResponse getPerson(final AuthValues authToken, final UUID garId, final UUID personId) throws WorkflowException;
}

package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

import java.util.UUID;

public interface PersonClient  extends DataClient<PersonClient> {

    PersonWithIdResponse createPerson(final AuthValues authValues, Person person)throws WorkflowException;

    PersonWithIdResponse updatePerson(final AuthValues authValues, UUID personId, Person clientPerson)throws WorkflowException;

    PersonWithIdResponse retrievePerson(final AuthValues authValues, UUID personId)throws WorkflowException;
}

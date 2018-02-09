package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

import java.util.List;
import java.util.UUID;

public interface PersonClient  extends DataClient<PersonClient> {

    PersonWithIdResponse createPerson(final AuthValues authValues, final Person person)throws WorkflowException;

    PersonWithIdResponse updatePerson(final AuthValues authValues, final UUID personId, Person clientPerson)throws WorkflowException;

    PersonWithIdResponse retrievePerson(final AuthValues authValues, final UUID personId)throws WorkflowException;

	List<PersonWithIdResponse> getBulk(final AuthValues authValues, final List<UUID> peopleUuids) throws WorkflowException;

}

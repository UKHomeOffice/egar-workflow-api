package uk.gov.digital.ho.egar.workflow.model.rest.bulk;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

@Data
@EqualsAndHashCode(callSuper=false)
public class PeopleBulkResponse {
	 private List<PersonWithIdResponse> people;
}

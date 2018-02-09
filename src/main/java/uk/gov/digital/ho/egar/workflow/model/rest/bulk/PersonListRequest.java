package uk.gov.digital.ho.egar.workflow.model.rest.bulk;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PersonListRequest {
	@JsonProperty("person_uuids")
	List<UUID> personUuids;
}

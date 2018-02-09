package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.validation.AddingPersonRequiresUuidOrDetails;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AddingPersonRequiresUuidOrDetails
public class PersonWithId extends Person {

    @JsonProperty(value = "person_uuid")
    private UUID personId;
}

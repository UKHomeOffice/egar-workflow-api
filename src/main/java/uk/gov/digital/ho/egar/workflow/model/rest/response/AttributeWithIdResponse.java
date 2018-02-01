package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;

import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeWithIdResponse extends Attribute {

    @JsonIgnore
    private UUID attributeUuid;
}

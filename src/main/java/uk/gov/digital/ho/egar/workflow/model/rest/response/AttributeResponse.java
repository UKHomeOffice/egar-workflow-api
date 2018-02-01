package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;

/**
 * The attribute response describing the attributes associated with a general aviation report.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeResponse extends Gar { //FIXME rename as AttributeGarResponse

    /**
     * The associated attributes.
     */
	@JsonProperty("attributes")
    private Attribute attribute;
}

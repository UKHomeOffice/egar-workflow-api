package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;

/**
 * The attributes that can be associated with a General aviation report.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Attribute {

    /**
     * Whether there are hazardous goods on board.
     */
    private Boolean hazardous;


    @JsonProperty("responsible_person")
    @Valid
    private ResponsiblePersonDetails otherDetails;

}

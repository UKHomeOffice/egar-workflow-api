package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResponsiblePersonDetails {

    @JsonProperty("type")
    private ResponsiblePersonType type;

    @JsonProperty("name")
    @Size(max = 35)
    private String name;

    @JsonProperty("contact_number")
    @Size(max = 35)
    private String contactNumber;
    
    @JsonProperty("address")
    private String address;
}

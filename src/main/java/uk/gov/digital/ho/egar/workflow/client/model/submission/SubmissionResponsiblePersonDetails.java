package uk.gov.digital.ho.egar.workflow.client.model.submission;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SubmissionResponsiblePersonDetails {
	
	@JsonProperty("type")
    private SubmissionResponsiblePersonType type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("contact_number")
    private String contactNumber;
    
    @JsonProperty("address")
    private String address;

}

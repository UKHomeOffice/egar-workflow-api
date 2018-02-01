package uk.gov.digital.ho.egar.workflow.client.model.submission;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SubmissionAttribute {
	
	private Boolean hazardous;

	@JsonProperty("responsible_person")
    private SubmissionResponsiblePersonDetails otherDetails;
}

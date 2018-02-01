package uk.gov.digital.ho.egar.workflow.client.model.submission;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SubmissionPerson {
	
	@NotNull
	private SubmissionPersonType type;
	

	 @JsonProperty("details")
	 private SubmissionPersonDetails personDetails;
	 
}

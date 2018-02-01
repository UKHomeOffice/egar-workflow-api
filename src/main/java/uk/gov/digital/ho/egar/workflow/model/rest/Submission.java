package uk.gov.digital.ho.egar.workflow.model.rest;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Submission {

	 @NotNull
	 private SubmissionType type; 

	 @JsonProperty("external_ref")
	 private String externalRef;
	 
	 @JsonProperty("status")
	 private SubmissionStatus status;
	 
	 @JsonProperty("reason")
	 private String reason; 
	 
}


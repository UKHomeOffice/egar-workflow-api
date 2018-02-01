package uk.gov.digital.ho.egar.workflow.model.rest.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Submission;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionWithId extends Submission{

	@JsonProperty(value = "submission_uuid")
    private UUID submissionUuid;
	
}

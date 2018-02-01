package uk.gov.digital.ho.egar.workflow.client.model.submission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionFiles {
	
    @JsonProperty(value = "file_name")
    private String fileName;

    @JsonProperty(value = "upload_complete")
    private boolean uploadComplete;

}

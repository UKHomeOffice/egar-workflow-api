package uk.gov.digital.ho.egar.workflow.client.model.submission;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionPeopleSummary {

	@JsonProperty(value = "captain")
    private SubmissionPerson captain;

    @JsonProperty(value = "crew")
    private List<SubmissionPerson> crew;

    @JsonProperty(value = "passengers")
    private List<SubmissionPerson> passengers;
}

package uk.gov.digital.ho.egar.workflow.client.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionAircraft;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionAttribute;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionFiles;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionLocation;
import uk.gov.digital.ho.egar.workflow.client.model.submission.SubmissionPeopleSummary;

@Data
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true ) 
public class ClientSubmission {
	
	@JsonProperty(value = "gar_uuid")
    private UUID garUuid;
	
	@JsonProperty("aircraft")
	private SubmissionAircraft aircraft;

	@JsonProperty("location")
	private List<SubmissionLocation> location;

	@JsonProperty("people")
	private SubmissionPeopleSummary people;

	@JsonProperty("files")
	private List<SubmissionFiles> files; //TODO

	@JsonProperty("attributes")
	private SubmissionAttribute attributes;
}

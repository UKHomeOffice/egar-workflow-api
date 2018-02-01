package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GarSummary extends Gar { 

    @JsonProperty("aircraft")
    private AircraftWithId aircraft;

    @JsonProperty("location")
    private List<LocationWithId> location;

    @JsonProperty("people")
    private PeopleSummary people;

    @JsonProperty("files")
    private List<FileWithIdResponse> files;

    @JsonProperty("attributes")
    private Attribute attributes;
    
    @JsonProperty(value = "submission")
    private SubmissionWithId submission;

	public GarSummary copy(Gar existing) {
		
		super.copy(existing);
		
		return this;
	}

}

package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeopleSummary {

    @JsonProperty(value = "captain")
    private PersonWithIdResponse captain;

    @JsonProperty(value = "crew")
    private List<PersonWithIdResponse> crew;

    @JsonProperty(value = "passengers")
    private List<PersonWithIdResponse> passengers;

}

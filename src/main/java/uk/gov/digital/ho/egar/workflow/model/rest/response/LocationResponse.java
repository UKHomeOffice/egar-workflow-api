package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocationResponse extends Gar { //FIXME rename as LocationGarResponse

    @JsonProperty("location")
    private LocationWithId location;

}

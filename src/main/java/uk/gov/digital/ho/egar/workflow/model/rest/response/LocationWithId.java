package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocationWithId extends Location {

    @JsonProperty("location_id")
    private UUID locationId;

    @JsonProperty("leg_num")
    private int legNo;

    @JsonProperty("leg_count")
    private int legCount;
}

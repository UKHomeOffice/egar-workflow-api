package uk.gov.digital.ho.egar.workflow.model.rest.response;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocationListResponse extends Gar { //FIXME rename as LocationListGarResponse


    @JsonProperty("location_uuids")
    private List<UUID> locationIds;
}

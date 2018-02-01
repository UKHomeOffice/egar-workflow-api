package uk.gov.digital.ho.egar.workflow.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode(callSuper=true)
public class ClientLocation extends Location {
	
    @JsonProperty("location_uuid")
    private UUID locationUuid;

    @JsonIgnore
    private UUID userUuid;
}

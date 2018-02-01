package uk.gov.digital.ho.egar.workflow.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class ClientAircraft {
    private String registration;

    private String type;

    private String base;

    private Boolean taxesPaid;

    @JsonProperty("aircraft_uuid")
    private UUID aircraftUuid;

    private UUID userUuid;
}

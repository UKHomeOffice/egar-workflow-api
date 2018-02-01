package uk.gov.digital.ho.egar.workflow.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class ClientGarList {

    @JsonProperty("gar_uuids")
    private List<UUID> garIds;
}

package uk.gov.digital.ho.egar.workflow.model.rest.bulk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class GarList {
    @JsonProperty(value = "gar_uuids")
    private List<UUID> garIds;

}

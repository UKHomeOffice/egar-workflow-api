package uk.gov.digital.ho.egar.workflow.model.rest.response;
 
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;

/**
 * A persisted version of the data, hence the ID.
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AircraftWithId extends Aircraft {

    @JsonIgnore
    private UUID aircraftUuid;

}

package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilesListGarResponse extends Gar {

    @JsonProperty("file_uuids")
    private List<UUID> fileUuids;

}

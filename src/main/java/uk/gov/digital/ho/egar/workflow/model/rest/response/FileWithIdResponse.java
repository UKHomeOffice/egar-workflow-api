package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileWithIdResponse{

    @JsonProperty(value = "file_uuid")
    private UUID fileUuid;

    @JsonProperty(value = "file_status")
    private FileStatus fileStatus;
    
    @JsonProperty("file_name")
	private String fileName;

	@JsonProperty("file_size")
	private long fileSize;

    @JsonProperty("file_link")
    private String fileLink;

}

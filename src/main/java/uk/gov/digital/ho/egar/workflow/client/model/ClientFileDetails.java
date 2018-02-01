package uk.gov.digital.ho.egar.workflow.client.model;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ClientFileDetails extends ClientFileInformation{

	@JsonProperty("file_uuid")
	private UUID fileUuid;

	@JsonProperty("file_status")
	private ClientFileStatus fileStatus;

	private UUID userUuid;

}

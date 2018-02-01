package uk.gov.digital.ho.egar.workflow.client.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ClientFileInformation {

	@JsonProperty("file_name")
	private String fileName;
	
	@JsonProperty("file_size")
	private long fileSize;

	@JsonProperty("file_link")
	private String fileLink;
}

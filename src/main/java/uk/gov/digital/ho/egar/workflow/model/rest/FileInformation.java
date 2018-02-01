package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FileInformation {
	
	@JsonProperty("file_name")
	private String fileName;
	
	@JsonProperty("file_size")
	private long fileSize;

	@JsonProperty("file_link")
	private String fileLink;

}

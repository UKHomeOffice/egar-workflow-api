package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FileDetails {
	
	@JsonProperty("file_link")
	private String fileLink;

}

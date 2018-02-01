package uk.gov.digital.ho.egar.workflow.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ClientFileInformationRequest {

	@JsonProperty("file_link")
	private String fileLink;
}

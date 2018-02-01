package uk.gov.digital.ho.egar.workflow.client.model.submission;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubmissionLocation {

	@JsonProperty("leg_num")
	private int legNo;

	@JsonProperty("leg_count")
	private int legCount;

	@ApiModelProperty(dataType = "String")
	@JsonProperty("datetime")
	private ZonedDateTime dateTime;

	@JsonProperty("ICAO")
	private String icaoCode;

	@JsonProperty("IATA")
	private String iataCode;

	private SubmissionPoint point;

}



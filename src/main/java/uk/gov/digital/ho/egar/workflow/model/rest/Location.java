package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.Point;

import java.time.ZonedDateTime;

import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode()
public class Location {

    @ApiModelProperty(dataType = "String")
    @JsonProperty("datetime")
    private ZonedDateTime dateTime;

    /** 
     * The CBP ICD states 13 characters, so can accept the larger ICAO codes.
     * think the issue is that there are some pseudo codes which are also valid and have numbers and – in them. There is no set format.
     **/
    @JsonProperty("ICAO")
    @Pattern(regexp = "^[0-9a-zA-Z\\-]{1,13}$" ,message="Airport ICAO not four character Uppercase.") 
    private String icaoCode;

    /** 
     * The CBP ICD states 13 characters, so can accept the larger IATA codes.
     * think the issue is that there are some pseudo codes which are also valid and have numbers and – in them. There is no set format.
     **/
    @JsonProperty("IATA")
    @Pattern(regexp = "^[0-9a-zA-Z\\-]{1,13}$" ,message="Airport IATA not Three character Uppercase.") 
    private String iataCode;

    private Point point;
}

package uk.gov.digital.ho.egar.workflow.model.rest;

import java.util.UUID;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GarSearchDetails {
	
	@Field
	@JsonProperty("user_uuid")
	private UUID userUuid;
	@Field
	@JsonProperty("gar_uuid")
	private UUID garUuid;
	@Field
	@JsonProperty("aircraft_registration")
	private String aircraftReg;
	
	@Field
	@JsonProperty("location_ICAO")
	private Location departureLocation; // should this be location. if so how would you search?
//	@JsonProperty("date_of_departure")
//	private LocalDate dateOfDeparture;
	
	public boolean matchesSearchCriteria(String searchCriteria) {
		
		if (garUuid!=null && garUuid.toString().equalsIgnoreCase(searchCriteria)) {
			return true;
		}
		
		if (aircraftReg!=null && aircraftReg.equalsIgnoreCase(searchCriteria)) {
			return true;
		}
		
		if (departureLocation!=null && departureLocation.getIcaoCode()!= null && departureLocation.getIcaoCode().equalsIgnoreCase(searchCriteria)) {
			return true;
		}
		
		return false;
		
	}
	
}

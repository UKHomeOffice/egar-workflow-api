package uk.gov.digital.ho.egar.workflow.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true ) 
public class ClientGar {
	
	@JsonProperty(value = "gar_uuid")
    private UUID garUuid;

	@JsonProperty(value = "user_uuid")
    private UUID userUuid;

	@JsonProperty(value = "aircraft_uuid")
    private UUID aircraftId;
	
	@JsonProperty(value = "submission_uuid")
	private UUID submissionId;
    
    @JsonProperty(value = "location_uuids")
    private List<UUID> locationIds;
    
    @JsonProperty(value = "file_uuids")
    private List<UUID> fileIds;

    @JsonProperty(value = "attributes")
    private Map<String, String> attributeMap;

    @JsonProperty(value = "people")
    private ClientGarPeople people;

    public ClientGar(){
        //initialising location to have a departure and arrival location.
        locationIds = new ArrayList<>();
        locationIds.add(null);
        locationIds.add(null);

        fileIds = new ArrayList<>();
    }
}

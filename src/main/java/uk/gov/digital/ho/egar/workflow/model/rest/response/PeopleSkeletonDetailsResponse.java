package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeopleSkeletonDetailsResponse {

    @JsonProperty(value = "captain")
    private UUID captain;

    @JsonProperty(value = "crew")
    private List<UUID> crew;

    @JsonProperty(value = "passengers")
    private List<UUID> passengers;

    public List<UUID> getPeopleUuids(){
    	
    	List<UUID> people= new ArrayList<>();
    	if (getCaptain() !=null){
    	people.add(getCaptain());
    	}
    	if(getCrew()!=null){
    		people.addAll(getCrew());
    	}
    	if(getPassengers()!=null){
    	people.addAll(getPassengers());
    	}
    	return people;
    	
    }
}

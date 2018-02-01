package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarSkeleton extends Gar{ 

    @JsonIgnore
    private UUID aircraftId;
    
    @JsonIgnore
    private UUID submissionId;

    @JsonProperty(value = "location_uuids")
    private List<UUID> locationIds;

    @JsonProperty(value = "file_uuids")
    private List<UUID> fileIds;

    @JsonProperty(value = "people")
    private PeopleSkeletonDetailsResponse people;

    @JsonIgnore
    private Map<String, String> attributeMap;
    
	public GarSkeleton copy(Gar existing) {
		
		super.copy(existing);
		
		return this ;
		
	}


	/**
	 * A test for attribute for the JSON.
	 * @return true if the data exists
	 */
	public boolean getAttributes()
	{
	return this.getAttributeMap() != null && !this.getAttributeMap().isEmpty() ;	
	}

	/**
	 * A test for aircraft for the JSON.
	 * @return true if the data exists
	 */
	public boolean getAircraft()
	{
	return this.getAircraftId() != null ;	
	}
	
	/**
	 * A test for submission for the JSON.
	 * @return true if the data exists
	 */
	public boolean getSubmission(){
		return this.getSubmissionId() != null;
	}
	 /**
	  * Null check on file Uuids
	  * @return Empty arrayList if fileIds  is null
	  */
	public List<UUID> getFileIds(){
		if (fileIds==null) {
			fileIds = new ArrayList<UUID>();
		}
		return fileIds;
	}

}

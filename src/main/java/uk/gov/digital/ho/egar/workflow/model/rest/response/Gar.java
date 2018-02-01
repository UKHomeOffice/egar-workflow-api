package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gar {

    @JsonProperty(value = "gar_uuid")
    private UUID garUuid;

    @JsonProperty(value = "user_uuid")
    private UUID userUuid;

	public Gar copy(Gar existing) {
		
		this.garUuid = existing.garUuid ; //TODO is there something in Lombok for this???
		this.userUuid = existing.userUuid ;
		
		return this ;
		
	}
}


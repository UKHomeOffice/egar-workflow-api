package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PeopleSkeletonResponse extends Gar{ //FIXME rename as PeopleSkeletonGarResponse

    @JsonProperty("people")
    private PeopleSkeletonDetailsResponse peopleSkeleton;

}

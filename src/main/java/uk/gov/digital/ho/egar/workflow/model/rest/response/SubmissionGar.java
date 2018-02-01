package uk.gov.digital.ho.egar.workflow.model.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This is the data returned by a GET on submition API.
 * It derives from GarResponse as it contains common fields such as gar_uuid.
 */
@Data
@EqualsAndHashCode(callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SubmissionGar extends Gar {

	@JsonProperty(value = "submission")
    private SubmissionWithId submission;
}


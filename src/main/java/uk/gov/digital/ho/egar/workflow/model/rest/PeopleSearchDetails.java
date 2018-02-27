package uk.gov.digital.ho.egar.workflow.model.rest;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeopleSearchDetails {
	@JsonProperty("given_name")
	String forename;
	@JsonProperty("family_name")
	String lastname;
	@JsonProperty("person_uuid")
	UUID personUuid;
}
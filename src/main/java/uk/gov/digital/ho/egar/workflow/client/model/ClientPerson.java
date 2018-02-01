package uk.gov.digital.ho.egar.workflow.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode
public class ClientPerson {
	
	@JsonProperty("given_name")
    private String givenName;
	@JsonProperty("family_name")
    private String familyName;

    private String gender;

    private String address;

    private LocalDate dob;

    private String place;

    private String nationality;
    @JsonProperty("document_type")
    private String documentType;
    @JsonProperty("document_no")
    private String documentNo;
    @JsonProperty("document_expiryDate")
    private LocalDate documentExpiryDate;
    @JsonProperty("document_issuingCountry")
    private String documentCountryCode;

    @JsonProperty("person_uuid")
    private UUID personUuid;

    private UUID userUuid;
}

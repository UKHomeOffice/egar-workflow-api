package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper=false)
public class PersonDetails {

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
}

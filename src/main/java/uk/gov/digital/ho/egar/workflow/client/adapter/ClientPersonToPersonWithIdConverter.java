package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.client.model.ClientPerson;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

@Component
public class ClientPersonToPersonWithIdConverter implements Converter<ClientPerson, PersonWithIdResponse> {

    @Override
    public PersonWithIdResponse convert(final ClientPerson source) {

        PersonWithIdResponse target = new PersonWithIdResponse();
        PersonDetails innerTarget = new PersonDetails();

        innerTarget.setAddress(source.getAddress());
        innerTarget.setDob(source.getDob());
        innerTarget.setDocumentCountryCode(source.getDocumentCountryCode());
        innerTarget.setDocumentExpiryDate(source.getDocumentExpiryDate());
        innerTarget.setDocumentNo(source.getDocumentNo());
        innerTarget.setDocumentType(source.getDocumentType());
        innerTarget.setFamilyName(source.getFamilyName());
        innerTarget.setGivenName(source.getGivenName());
        innerTarget.setGender(source.getGender());
        innerTarget.setNationality(source.getNationality());
        innerTarget.setPlace(source.getPlace());

        target.setPersonDetails(innerTarget);
        target.setPersonId(source.getPersonUuid());
        
        return target;
    }
}

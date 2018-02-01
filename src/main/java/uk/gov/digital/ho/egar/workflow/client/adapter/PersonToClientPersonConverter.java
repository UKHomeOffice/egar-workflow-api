package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientPerson;
import uk.gov.digital.ho.egar.workflow.client.model.ClientPersonType;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.PersonDetails;

@Component
public class PersonToClientPersonConverter implements Converter<Person, ClientPerson> {

    @Override
    public ClientPerson convert(final Person source) {

        
        ClientPerson target = new ClientPerson();

        PersonDetails innerSource = source.getPersonDetails();

        target.setAddress(innerSource.getAddress());
        target.setDob(innerSource.getDob());
        target.setDocumentCountryCode(innerSource.getDocumentCountryCode());
        target.setDocumentExpiryDate(innerSource.getDocumentExpiryDate());
        target.setDocumentNo(innerSource.getDocumentNo());
        target.setDocumentType(innerSource.getDocumentType());
        target.setFamilyName(innerSource.getFamilyName());
        target.setGivenName(innerSource.getGivenName());
        target.setGender(innerSource.getGender());
        target.setNationality(innerSource.getNationality());
        target.setPlace(innerSource.getPlace());
        
        return target;
    }
}

package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftWithId;

@Component
public class ClientAircraftToAircraftWithIdConverter implements Converter<ClientAircraft, AircraftWithId> {

    @Override
    public AircraftWithId convert(final ClientAircraft source) {

        AircraftWithId target = new AircraftWithId();
        target.setAircraftUuid(source.getAircraftUuid());
        target.setBase(source.getBase());
        target.setRegistration(source.getRegistration());
        target.setTaxesPaid(source.getTaxesPaid());
        target.setType(source.getType());

        return target;

    }
}

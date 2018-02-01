package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;

@Component
public class AircraftToClientAircraftConverter implements Converter<Aircraft, ClientAircraft> {

    @Override
    public ClientAircraft convert(final Aircraft source) {

        ClientAircraft target = new ClientAircraft();
        target.setBase(source.getBase());
        target.setRegistration(source.getRegistration());
        target.setTaxesPaid(source.getTaxesPaid());
        target.setType(source.getType());
        return target;
    }
}

package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientLocation;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;

@Component
public class LocationToClientLocationConverter implements Converter<Location, ClientLocation> {

    @Override
    public ClientLocation convert(final Location source) {

        ClientLocation target = new ClientLocation();
        target.setDateTime(source.getDateTime());
        target.setIcaoCode(source.getIcaoCode());
        target.setIataCode(source.getIataCode());

        target.setPoint(source.getPoint());
        return target;
    }
}

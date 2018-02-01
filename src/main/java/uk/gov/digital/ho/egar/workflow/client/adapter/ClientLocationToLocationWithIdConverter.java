package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientLocation;
import uk.gov.digital.ho.egar.workflow.model.rest.Point;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationWithId;

@Component
public class ClientLocationToLocationWithIdConverter implements Converter<ClientLocation, LocationWithId> {

    @Override
    public LocationWithId convert(final ClientLocation source) {

        LocationWithId target = new LocationWithId();
        target.setLocationId(source.getLocationUuid());
        target.setDateTime(source.getDateTime());
        target.setIcaoCode(source.getIcaoCode());
        target.setIataCode(source.getIataCode());

        Point targetPoint = new Point();
        Point sourcePoint = source.getPoint();
        if (sourcePoint!=null){
            targetPoint.setLatitude(sourcePoint.getLatitude());
            targetPoint.setLongitude(sourcePoint.getLongitude());
        }

        target.setPoint(targetPoint);

        return target;

    }
}

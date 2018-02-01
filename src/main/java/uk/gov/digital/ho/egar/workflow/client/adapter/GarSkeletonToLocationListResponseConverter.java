package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationListResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;

@Component
public class GarSkeletonToLocationListResponseConverter implements Converter<GarSkeleton, LocationListResponse> {

    @Override
    public LocationListResponse convert(final GarSkeleton source) {
        LocationListResponse target = new LocationListResponse();
        target.setGarUuid(source.getGarUuid());
        target.setUserUuid(source.getUserUuid());
        target.setLocationIds(source.getLocationIds());
        return target;

    }
}

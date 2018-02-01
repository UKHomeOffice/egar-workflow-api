package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSkeletonDetailsResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSkeletonResponse;

@Component
public class GarSkeletonToPeopleSkeletonResponseConverter implements Converter<GarSkeleton, PeopleSkeletonResponse> {

    @Override
    public PeopleSkeletonResponse convert(final GarSkeleton source) {

        PeopleSkeletonDetailsResponse peopleTarget = new PeopleSkeletonDetailsResponse();
        if (source.getPeople()!=null){
            PeopleSkeletonDetailsResponse peopleSource = source.getPeople();
            peopleTarget.setCrew(peopleSource.getCrew());
            peopleTarget.setCaptain(peopleSource.getCaptain());
            peopleTarget.setPassengers(peopleSource.getPassengers());
        }

        PeopleSkeletonResponse target = new PeopleSkeletonResponse();

        target.setPeopleSkeleton(peopleTarget);
        target.setUserUuid(source.getUserUuid());
        target.setGarUuid(source.getGarUuid());
        return target;

    }
}

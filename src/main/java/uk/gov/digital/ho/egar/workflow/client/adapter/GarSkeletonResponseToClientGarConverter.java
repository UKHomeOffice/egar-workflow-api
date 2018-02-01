package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGar;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGarPeople;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSkeletonDetailsResponse;

@Component
public class GarSkeletonResponseToClientGarConverter implements Converter<GarSkeleton, ClientGar> {

    @Override
    public ClientGar convert(final GarSkeleton source) {

        ClientGarPeople peopleTarget = new ClientGarPeople();
        if (source.getPeople()!=null){
            PeopleSkeletonDetailsResponse peopleSource = source.getPeople();
            peopleTarget.setCrew(peopleSource.getCrew());
            peopleTarget.setCaptain(peopleSource.getCaptain());
            peopleTarget.setPassengers(peopleSource.getPassengers());
        }

        ClientGar target = new ClientGar();
        target.setAircraftId(source.getAircraftId());
        target.setPeople(peopleTarget);
        target.setLocationIds(source.getLocationIds());
        target.setFileIds(source.getFileIds());
        target.setUserUuid(source.getUserUuid());
        target.setGarUuid(source.getGarUuid());
        target.setAttributeMap(source.getAttributeMap());
        target.setSubmissionId(source.getSubmissionId());
        return target;

    }
}

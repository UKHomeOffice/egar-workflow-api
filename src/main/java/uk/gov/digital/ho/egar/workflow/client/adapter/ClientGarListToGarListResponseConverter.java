package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGarList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;

@Component
public class ClientGarListToGarListResponseConverter implements Converter<ClientGarList, GarListResponse> {

    @Override
    public GarListResponse convert(final ClientGarList source) {

        GarListResponse target = new GarListResponse();
        target.setGarIds(source.getGarIds());
        return target;

    }
}

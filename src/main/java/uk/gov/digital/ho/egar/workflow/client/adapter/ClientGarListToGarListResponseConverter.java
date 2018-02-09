package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGarList;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;

@Component
public class ClientGarListToGarListResponseConverter implements Converter<ClientGarList, GarList> {

    @Override
    public GarList convert(final ClientGarList source) {

        GarList target = new GarList();
        target.setGarIds(source.getGarIds());
        return target;

    }
}

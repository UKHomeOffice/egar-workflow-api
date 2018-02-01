package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientAttribute;
import uk.gov.digital.ho.egar.workflow.client.model.ClientResponsibleType;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.ResponsiblePersonDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.ResponsiblePersonType;

@Component
public class AttributeToClientAttributeConverter implements Converter<Attribute, ClientAttribute> {

    @Override
    public ClientAttribute convert(final Attribute source) {

        ClientAttribute target = new ClientAttribute();

        ResponsiblePersonDetails sourceDetails = source.getOtherDetails();
        if (sourceDetails!=null){
            ResponsiblePersonType sourceResponsible = sourceDetails.getType();
            if (sourceResponsible!=null){
                target.setResponsibleType(ClientResponsibleType.valueOf(sourceResponsible.name()));
            }
            target.setResponsibleContactNo(sourceDetails.getContactNumber());
            target.setResponsibleName(sourceDetails.getName());
            target.setResponsibleAddress(sourceDetails.getAddress());
        }

        target.setHazardous(source.getHazardous());
        return target;
    }
}

package uk.gov.digital.ho.egar.workflow.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class ClientAttribute {
    private Boolean hazardous;

    private ClientResponsibleType responsibleType;

    private String responsibleName;

    private String responsibleContactNo;
    
    private String responsibleAddress;

    private UUID attributeUuid;

    private UUID userUuid;
}

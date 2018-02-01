package uk.gov.digital.ho.egar.workflow.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@EqualsAndHashCode
public class ClientGarPeople {

    private UUID captain;

    private List<UUID> crew;

    private List<UUID> passengers;

    public ClientGarPeople(){
        crew = new ArrayList<>();
        passengers = new ArrayList<>();
    }
}

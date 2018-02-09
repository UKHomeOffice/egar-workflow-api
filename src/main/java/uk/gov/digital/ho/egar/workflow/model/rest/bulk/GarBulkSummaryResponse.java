package uk.gov.digital.ho.egar.workflow.model.rest.bulk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class GarBulkSummaryResponse {
    private List<GarSummary> gars;

}


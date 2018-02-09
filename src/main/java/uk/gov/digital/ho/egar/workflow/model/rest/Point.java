package uk.gov.digital.ho.egar.workflow.model.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Point {

    private String latitude;

    private String longitude;

}

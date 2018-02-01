package uk.gov.digital.ho.egar.workflow.model.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=false)
public class Point {

    private String latitude;

    private String longitude;

}

package uk.gov.digital.ho.egar.workflow.model.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The basic data sent into hold the data.
 * This is part of the request model.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Aircraft { 

    private String registration;

    private String type;

    private String base;

    private Boolean taxesPaid;

}

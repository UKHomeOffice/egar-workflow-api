package uk.gov.digital.ho.egar.workflow.client.model.submission;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Aircraft data held by submission client
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SubmissionAircraft {

	private String registration;

	private String type;

	private String base;

	private Boolean taxesPaid;
}

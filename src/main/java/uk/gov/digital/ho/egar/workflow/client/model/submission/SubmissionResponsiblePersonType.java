package uk.gov.digital.ho.egar.workflow.client.model.submission;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum SubmissionResponsiblePersonType {

	OTHER,
	CAPTAIN;

	@JsonCreator
	public static SubmissionResponsiblePersonType forValue(String value) {
		if (value == null){
			return null;
		}

		return SubmissionResponsiblePersonType.valueOf(StringUtils.upperCase(value));

	}

	@JsonValue
	public String toValue() {
		return this.name();
	}
}

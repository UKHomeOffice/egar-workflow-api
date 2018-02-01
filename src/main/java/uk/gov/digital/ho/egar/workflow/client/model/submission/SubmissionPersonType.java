package uk.gov.digital.ho.egar.workflow.client.model.submission;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum SubmissionPersonType {

	CAPTAIN,
    CREW,
    PASSENGER;

    @JsonCreator
    public static SubmissionPersonType forValue(String value)  {
        if (value == null){
            return null;
        }

        return SubmissionPersonType.valueOf(StringUtils.upperCase(value));
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

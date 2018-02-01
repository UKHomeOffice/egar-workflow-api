package uk.gov.digital.ho.egar.workflow.model.rest;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FileStatus {
	UPLOADING,
	UPLOAD_FAILED,
	AWAITING_VIRUS_SCAN,
	VIRUS_SCANNED,
	QUARANTINED;

	@JsonCreator
	public static FileStatus forValue(String value)  {
		if (value == null){
			return null;
		}
		return FileStatus.valueOf(StringUtils.upperCase(value));
	}

	@JsonValue
	public String toValue() {
		return this.name();
	}
}

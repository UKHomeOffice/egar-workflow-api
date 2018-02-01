package uk.gov.digital.ho.egar.workflow.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang.StringUtils;

public enum SubmissionStatus {

	PENDING,
	SUBMITTED,
	CANCELLED,
	FAILED;
	
	 @JsonCreator
	 public static SubmissionStatus forValue(String value){
		 if (value == null){
	            return null;
	        }
		 
		 return SubmissionStatus.valueOf(StringUtils.upperCase(value));
	 }
	 
	 @JsonValue
	 public String toValue() {
	      return this.name();
	 }
}

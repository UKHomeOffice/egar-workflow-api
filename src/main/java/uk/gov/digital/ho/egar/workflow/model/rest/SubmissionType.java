package uk.gov.digital.ho.egar.workflow.model.rest;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubmissionType {

	CBP_STT;

	
	 @JsonCreator
	 public static SubmissionType forValue(String value){
		 if (value == null){
	            return null;
	        }
		 
		 return SubmissionType.valueOf(StringUtils.upperCase(value));
	 }
	 
	 @JsonValue
	 public String toValue() {
	      return this.name();
	 }
}

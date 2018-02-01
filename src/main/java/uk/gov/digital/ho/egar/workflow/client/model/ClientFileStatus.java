package uk.gov.digital.ho.egar.workflow.client.model;

public enum ClientFileStatus {

	UPLOADING,
	UPLOAD_FAILED,
	AWAITING_VIRUS_SCAN,
	VIRUS_SCANNED,
	QUARANTINED;
}

package uk.gov.digital.ho.egar.workflow.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileIdWithGarResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FilesListGarResponse;

public interface FileRestService {

	/**
     * Uploads file to a general aviation report
     * @param garUuid The gar uuid.
     */
	ResponseEntity<Void> uploadFile(final String authToken, 
									final UUID uuidOfUser, 
									final UUID garUuid, 
									final FileDetails file) throws WorkflowException;

	/**
     * Retrieves a list of file uuids on an existing gar
     * @param garUuidThe gar uuid.
     * @return The file list response.
     * @throws WorkflowException 
     */
	FilesListGarResponse retrieveFileList(final String authToken, 
										  final UUID uuidOfUser, 
										  final UUID garUuid) throws WorkflowException;

	/**
     * Gets an existing file on an existing general aviation report.
     * @param garUuidthe gar uuid.
     * @param fileUuid the file uuid.
     * @return The file response
     */
	FileIdWithGarResponse retrieveFile(final String authToken, 
							  final UUID uuidOfUser, 
							  final UUID garUuid, 
							  final UUID fileUuid) throws WorkflowException;

	 /**
     * Delete an existing person on an existing general aviation report.
     * @param garUuidthe gar uuid.
     * @param fileUuid The file uuid.
     * @return The response
     * @throws WorkflowException 
     */
	ResponseEntity<Void> deleteFile(final String authToken, 
									final UUID uuidOfUser, 
			     					final UUID garUuid, 
			     					final UUID fileUuid) throws WorkflowException;

}
package uk.gov.digital.ho.egar.workflow.service;

import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileIdWithGarResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FilesListGarResponse;

/**
 * The file service providing interactions with a general aviation reports people.
 *
 */
public interface FileService {

	/**
     * Uploads a file for an existing gar
     * @param garUuid the gar uuid
     * @param file the file to add
     * @return The file uuid.
     * @throws WorkflowException 
     */
	UUID uploadFileDetails(final AuthValues authValues, 
					final UUID garUuid, 
					final FileDetails fileDetails) throws WorkflowException;

	/**
     * Retrieves a hierarchy of file identifiers on a gar.
     * @param garUuid The gar uuid.
     * @return The file list response
     * @throws WorkflowException 
     */
	FilesListGarResponse getAllFiles(final AuthValues authValues, 
									 final UUID garUuid) throws WorkflowException;

	/**
     * Retrieves an existing file from an existing gar
     * @param garUuid The gar uuid
     * @param fileUuid the file uuid
     * @return The file details
     * @throws WorkflowException 
     */
	FileIdWithGarResponse getFile(final AuthValues authValues, 
						 final UUID garUuid, 
						 final UUID fileUuid) throws WorkflowException;

	/**
     * Deletes an existing file from an existing gar
     * @param garUuid The gar uuid
     * @param fileUuid the person uuid
     * @throws WorkflowException
     */
	void deleteFile(final AuthValues authValues, 
					final UUID garUuid, 
					final UUID fileUuid) throws WorkflowException;

}

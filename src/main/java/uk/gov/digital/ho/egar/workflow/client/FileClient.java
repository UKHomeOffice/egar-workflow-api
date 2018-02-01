package uk.gov.digital.ho.egar.workflow.client;

import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;

public interface FileClient extends DataClient<FileClient>{

	FileWithIdResponse uploadFileInformation(final AuthValues authValues,
											 final FileInformation fileInfo) throws WorkflowException;

	FileWithIdResponse retrieveFileDetails(final AuthValues authValues, 
							   			   final UUID fileUuid) throws WorkflowException;

}

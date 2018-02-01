package uk.gov.digital.ho.egar.workflow.service.behaviour;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.ForbiddenFileUploadWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.FileClient;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;

import java.util.List;
import java.util.UUID;

@Component
public class FileCheck implements FileChecker{

	@Autowired
	private FileClient fileClient;

	@Autowired
	private WorkflowPropertiesConfig config;

	public void checkMaxSizeOfTotalFiles(final AuthValues authValues, final List<UUID> fileIds, final long fileSize)
			throws WorkflowException{

		long totalFileSize = 0;
		if(fileIds !=null){
			for(UUID fileUuid:fileIds){

				FileWithIdResponse fileWithID = fileClient.retrieveFileDetails(authValues, fileUuid);
				long size = fileWithID.getFileSize();
				totalFileSize += size;

				if ((totalFileSize + fileSize) > config.getMaxTotalFileSize())
					throw new ForbiddenFileUploadWorkflowException(String.format("Maximum total file size is %s",config.getMaxTotalFileSize()));
			}
		}
		if (fileSize > config.getMaxTotalFileSize())
			throw new ForbiddenFileUploadWorkflowException(String.format("Maximum total file size is %s",config.getMaxTotalFileSize()));

	}

	public void checkIndividualFileSize(final long fileSize) throws ForbiddenFileUploadWorkflowException {
		if(fileSize > config.getMaxFileSize())
			throw new ForbiddenFileUploadWorkflowException(String.format("Maximum file size is %s",config.getMaxFileSize()));
	}


	public void checkNumberOfFiles(final List<UUID> fileIds) throws ForbiddenFileUploadWorkflowException {
		if(fileIds.size() >= config.getMaxFileNumber())
			throw new ForbiddenFileUploadWorkflowException(String.format("Only allowed to upload %s files",config.getMaxFileNumber()));
	}
}

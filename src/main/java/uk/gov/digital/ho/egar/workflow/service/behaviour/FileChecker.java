package uk.gov.digital.ho.egar.workflow.service.behaviour;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.ForbiddenFileUploadWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;

import java.util.List;
import java.util.UUID;

public interface FileChecker {

	void checkMaxSizeOfTotalFiles(final AuthValues authValues, final List<UUID> fileIds, final long fileSize)
			throws WorkflowException;
	void checkIndividualFileSize(final long fileSize) throws ForbiddenFileUploadWorkflowException;
	void checkNumberOfFiles(final List<UUID> fileIds) throws ForbiddenFileUploadWorkflowException;
}

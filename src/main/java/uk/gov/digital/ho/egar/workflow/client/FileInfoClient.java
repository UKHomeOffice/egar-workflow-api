package uk.gov.digital.ho.egar.workflow.client;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;

public interface FileInfoClient extends DataClient<FileInfoClient>{

	FileInformation retrieveFileInformation(final AuthValues authValues,
                                            final FileDetails fileDetails) throws WorkflowException;

}

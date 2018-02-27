package uk.gov.digital.ho.egar.workflow.client.dummy;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.FileInfoClient;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;


@Component
@Profile({"mock-file"})
public class DummyFileInfoClientImpl extends DummyClient<FileInfoClient> implements FileInfoClient,InfoContributor {

	private long fileSize = 131072;

	@Override
	public FileInformation retrieveFileInformation(AuthValues authValues, FileDetails fileDetails) throws WorkflowException {
		
		FileInformation fileInfo = new FileInformation();
		
		fileInfo.setFileLink(fileDetails.getFileLink());
		
		String[] parts = fileDetails.getFileLink().split("[\\/\\\\]");
		fileInfo.setFileName(parts[parts.length-1]);
		fileInfo.setFileSize(fileSize);

		return fileInfo;
	}

	
	public void setFileSize(final long fileSize){
		this.fileSize = fileSize;
	}

}

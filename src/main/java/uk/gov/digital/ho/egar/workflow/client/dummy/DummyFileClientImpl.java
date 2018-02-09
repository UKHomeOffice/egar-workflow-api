package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.FileClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileDetails;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;


@Component
@Profile({"mock-file"})
public class DummyFileClientImpl extends DummyClient<FileClient> implements FileClient,InfoContributor {

	private final Map<DummyKey,ClientFileDetails> dummyFileRepo = new HashMap<>();

	@Autowired
	private ConversionService conversionService;

	private ClientFileStatus status = ClientFileStatus.AWAITING_VIRUS_SCAN;

	@Override
	public FileWithIdResponse uploadFileInformation(AuthValues authValues,
													FileInformation fileInfo) throws WorkflowException {
		
		ClientFileDetails clientFile = conversionService.convert(fileInfo, ClientFileDetails.class);
		
		
		clientFile.setFileStatus(status);
		clientFile.setFileUuid(UUID.randomUUID());
		clientFile.setUserUuid(authValues.getUserUuid());

		clientFile = add(clientFile);
		
		
		return conversionService.convert(clientFile, FileWithIdResponse.class);
	}

	@Override
	public FileWithIdResponse retrieveFileDetails(AuthValues authValues, UUID fileUuid) throws WorkflowException {

		DummyKey key = new DummyKey(fileUuid,authValues.getUserUuid());
		
		ClientFileDetails clientResponse = dummyFileRepo.get(key);
		
		return conversionService.convert(clientResponse, FileWithIdResponse.class);
	}
	
	@Override
	public List<FileWithIdResponse> getBulk(AuthValues authValues, List<UUID> fileUuids) {
		
		List<FileWithIdResponse> files = new ArrayList<>();
		for(UUID fileUuid: fileUuids){
			DummyKey key = new DummyKey(fileUuid,authValues.getUserUuid());
			ClientFileDetails clientResponse = dummyFileRepo.get(key);
			FileWithIdResponse file = conversionService.convert(clientResponse, FileWithIdResponse.class);
			
			files.add(file);
		}
		return files;
	}
	
	private ClientFileDetails add(final ClientFileDetails clientFile) {
		dummyFileRepo.put(new DummyKey(clientFile.getFileUuid(), clientFile.getUserUuid()), clientFile);
		return clientFile;
	}

	public void setFileStatus(FileStatus fileStatus) {
		this.status=ClientFileStatus.valueOf(fileStatus.toString());
	}

}

package uk.gov.digital.ho.egar.workflow.client.dummy;

import java.util.HashMap;
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
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;


@Component
@Profile({"mock-file"})
public class DummyFileClientImpl extends DummyClient<FileClient> implements FileClient,InfoContributor {

	private final Map<DummyKey,ClientFileDetails> dummyFileRepo = new HashMap<>();

	@Autowired
	private ConversionService conversionService;

	@Override
	public FileWithIdResponse uploadFileInformation(AuthValues authValues,
													FileInformation fileInfo) throws WorkflowException {
		
		ClientFileDetails clientFile = conversionService.convert(fileInfo, ClientFileDetails.class);
		
		clientFile.setFileStatus(ClientFileStatus.AWAITING_VIRUS_SCAN);
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
	
	private ClientFileDetails add(final ClientFileDetails clientFile) {
		dummyFileRepo.put(new DummyKey(clientFile.getFileUuid(), clientFile.getUserUuid()), clientFile);
		return clientFile;
	}

}

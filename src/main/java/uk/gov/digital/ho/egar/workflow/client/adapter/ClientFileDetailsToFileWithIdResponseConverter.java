package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.client.model.ClientFileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;

@Component
public class ClientFileDetailsToFileWithIdResponseConverter implements Converter<ClientFileDetails, FileWithIdResponse>{

	@Override
	public FileWithIdResponse convert(ClientFileDetails source) {
		

		FileWithIdResponse target = new FileWithIdResponse();
		
		target.setFileUuid(source.getFileUuid());
		target.setFileName(source.getFileName());
		target.setFileStatus(FileStatus.valueOf(source.getFileStatus().toString()));
		target.setFileLink(source.getFileLink());
		target.setFileSize(source.getFileSize());
		
		return target;
	}

}

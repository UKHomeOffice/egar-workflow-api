package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.client.model.ClientFileInformation;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;

@Component
public class ClientFileInfoToFileDetailsConvertor implements Converter<ClientFileInformation, FileInformation>{

	@Override
	public FileInformation convert(ClientFileInformation source) {
		FileInformation target = new FileInformation();
		
		target.setFileLink(source.getFileLink());
		target.setFileName(source.getFileName());
		target.setFileSize(source.getFileSize());
		
		return target;
	}
	

}

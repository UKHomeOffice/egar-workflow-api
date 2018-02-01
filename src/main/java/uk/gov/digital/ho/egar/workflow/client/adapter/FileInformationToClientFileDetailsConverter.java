package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.client.model.ClientFileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;

@Component
public class FileInformationToClientFileDetailsConverter implements Converter<FileInformation, ClientFileDetails>{

	@Override
	public ClientFileDetails convert(final FileInformation source) {

		ClientFileDetails target = new ClientFileDetails();
		
		target.setFileLink(source.getFileLink());
		target.setFileName(source.getFileName());
		target.setFileSize(source.getFileSize());
		
		return target;
	}

}

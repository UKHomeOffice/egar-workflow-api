package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileDetails;
import uk.gov.digital.ho.egar.workflow.client.model.ClientFileInformationRequest;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;

@Component
public class FileDetailsToClientFileInformationRequestConverter implements Converter<FileDetails, ClientFileInformationRequest>{

	@Override
	public ClientFileInformationRequest convert(final FileDetails source) {

		ClientFileInformationRequest target = new ClientFileInformationRequest();
		
		target.setFileLink(source.getFileLink());

		return target;
	}

}

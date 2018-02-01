package uk.gov.digital.ho.egar.workflow.client.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.model.rest.response.FilesListGarResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;

@Component
public class GarSkeletonToFileListResponseConverter implements Converter<GarSkeleton, FilesListGarResponse> {

	@Override
	public FilesListGarResponse convert(GarSkeleton source) {

		FilesListGarResponse target = new FilesListGarResponse();
		
		target.setGarUuid(source.getGarUuid());
		target.setUserUuid(source.getUserUuid());
		target.setFileUuids(source.getFileIds());
		return target;
	}

}

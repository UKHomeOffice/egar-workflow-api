package uk.gov.digital.ho.egar.workflow.client.adapter;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.ResponsiblePersonDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;

import static uk.gov.digital.ho.egar.workflow.client.constants.AttributeKeys.HAZARDOUS_KEY;
import static uk.gov.digital.ho.egar.workflow.client.constants.AttributeKeys.RESPONSIBLE_DETAILS_KEY;

@Component
public class GarSkeletonToAttributeConverter implements Converter<GarSkeleton, AttributeResponse> {

	@Autowired
	private ObjectMapper mapper;

	private static final Logger LOG = LoggerFactory.getLogger(GarSkeletonToAttributeConverter.class);

	@Override
	public AttributeResponse convert(final GarSkeleton source) {

		AttributeResponse target = new AttributeResponse();
		Attribute innerTarget = new Attribute();

		Map<String, String> innerSource = source.getAttributeMap();

		if(innerSource !=null && !innerSource.isEmpty()){

			String responsibleDetailsString = innerSource.get(RESPONSIBLE_DETAILS_KEY);
			if (responsibleDetailsString == null){
				innerTarget.setOtherDetails(null);
			}else{
				try {
					ResponsiblePersonDetails responsibleDetails = mapper.readValue(responsibleDetailsString, ResponsiblePersonDetails.class);
					innerTarget.setOtherDetails(responsibleDetails);
				} catch (IOException e) {
					innerTarget.setOtherDetails(null);
					LOG.warn("Unable to deserialize responsible person detail", e);
				}
			}
			String hazardous = innerSource.get(HAZARDOUS_KEY);
			if(hazardous != null){
				innerTarget.setHazardous(Boolean.valueOf(hazardous));
			}
		}
		target.setAttribute(innerTarget);
		target.setGarUuid(source.getGarUuid());
		target.setUserUuid(source.getUserUuid());


		return target;

	}
}

package uk.gov.digital.ho.egar.workflow.client.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.digital.ho.egar.workflow.client.constants.AttributeKeys.HAZARDOUS_KEY;
import static uk.gov.digital.ho.egar.workflow.client.constants.AttributeKeys.RESPONSIBLE_DETAILS_KEY;

@Component
public class AttributeToMapConverter {

	@Autowired
	private ObjectMapper mapper;

	private static final Logger LOG = LoggerFactory.getLogger(AttributeToMapConverter.class);

	public Map<String, String> convert(final Attribute source) {

		Map<String, String > target = new HashMap<>();
		if(source.getHazardous()!=null){
			target.put(HAZARDOUS_KEY, source.getHazardous().toString());
		}

		if (source.getOtherDetails()!=null){
			try {
				String otherDetails = mapper.writeValueAsString(source.getOtherDetails());
				target.put(RESPONSIBLE_DETAILS_KEY, otherDetails);
			} catch (JsonProcessingException e) {
				LOG.warn("Unable to serialise responsible person detail", e);
			}
		}

		return target;

	}
}
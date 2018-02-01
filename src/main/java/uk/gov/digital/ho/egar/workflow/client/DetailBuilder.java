/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.client;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.ParameterizedType;

import uk.gov.digital.ho.egar.shared.util.monitoring.DownstreamHealthIndicator;

/**
 * This class will create a set of information for a /info end point.
 */
@Service
public class DetailBuilder {

	@Autowired
	private ObjectMapper mapper;

	public <T> void withDetail(DataClient<T> dataClient, Builder builder) {

		@SuppressWarnings("unchecked")
		Class<T> persistentClass = (Class<T>)
				   ((ParameterizedType)dataClient.getClass().getGenericSuperclass())
				      .getActualTypeArguments()[0];
		String getServiceName = persistentClass.getSimpleName();
		
		ObjectNode infoNode = mapper.createObjectNode();

		infoNode.put("class", dataClient.getClass().getSimpleName() );

		if (dataClient instanceof RestDataClient) {
			
			ObjectNode restInfoNode = mapper.createObjectNode();
			infoNode.set("rest", restInfoNode );
			
			RestDataClient restDataClient = (RestDataClient) dataClient;
			URL baseUrl = restDataClient.getBaseEndpointUrl();
			restInfoNode.put("url", baseUrl.toString());

			JsonNode downstreamHealth = DownstreamHealthIndicator.fetchRawHealth(restDataClient.getRestTemplate(),restDataClient.getEndpointServerRootUrl());

			if (downstreamHealth == null) {
				restInfoNode.put("accessable", "NO");
			} else {
				
				restInfoNode.put("accessable", "YES" );
				
				restInfoNode.set("health-info", downstreamHealth);

				JsonNode downstreamInfoNode = DownstreamHealthIndicator.fetchInfo(restDataClient.getRestTemplate(),
																				  restDataClient.getEndpointServerRootUrl());
				
				if (downstreamInfoNode != null) {
					restInfoNode.set("app-info", downstreamInfoNode);
				}

			}
		}

		builder.withDetail(getServiceName, infoNode);
	}
}

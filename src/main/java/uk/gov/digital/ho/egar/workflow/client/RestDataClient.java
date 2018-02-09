package uk.gov.digital.ho.egar.workflow.client;

import java.net.URL;

import org.springframework.web.client.RestTemplate;

/**
 * This is a base class which determines the type of operations that a <b>REST</b> client service will have to perform.
 * @param <T> The interface class this service will 'have'.
 */
public interface RestDataClient<T extends DataClient<?>> extends DataClient<T> {

	/**
	 * @return A URL for the service with no serverlet context.
	 */
	URL getBaseEndpointUrl();
	/**
	 * @return The configured URL URL of the server for this service.
	 */
	URL getEndpointServerRootUrl();
	RestTemplate getRestTemplate();
	
}

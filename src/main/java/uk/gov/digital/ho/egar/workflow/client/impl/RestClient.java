package uk.gov.digital.ho.egar.workflow.client.impl;

import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info.Builder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.shared.util.monitoring.DownstreamHealthIndicator;
import uk.gov.digital.ho.egar.workflow.api.exceptions.ClientErrorWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.DetailBuilder;
import uk.gov.digital.ho.egar.workflow.client.RestDataClient;
import uk.gov.digital.ho.egar.workflow.api.exceptions.BadUpstreamClientWorkflowException;
import uk.gov.digital.ho.egar.workflow.utils.impl.UriLocationUtilitiesImpl;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * This class abstracts the posting of the Auth values to the standard rest template.
 * @param <T> The interface class this service will 'have'.
 */
public abstract class RestClient<T extends DataClient<?>> implements RestDataClient<T>,InfoContributor  {

	/**
	 * slf4j logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);
	
	/** The target server URL */
	private final URL endpointBaseUrl ;

	/** The target server URL */
	private final URL endpointServerRootUrl ;
	
	/**
	 * Spring REST template.
	 */
	private final RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper ;
	
	@Autowired
	private DetailBuilder detailBuilder ;
	
	/**
	 * @param endpointUrl The target server URL
	 * @throws MalformedURLException 
	 */
	public RestClient(final String endpointUrl,
					  final RestTemplate restTemplate)  {
		super();
		this.restTemplate = restTemplate ;
		try {
			this.endpointBaseUrl = new URL(endpointUrl);
			this.endpointServerRootUrl = new URL(this.endpointBaseUrl.getProtocol(),this.endpointBaseUrl.getHost(),this.endpointBaseUrl.getPort(), "/");
		} catch (MalformedURLException e) {
			throw new UriLocationUtilitiesImpl.URIFormatException(e);
		}
	}
	
	
	protected URL createEndpointUrl(String uriSegment) {
		try {
			if ( uriSegment == null )
				return getBaseEndpointUrl();
			
			StringBuilder sb = new StringBuilder(getBaseEndpointUrl().toExternalForm());
			
			if ( !StringUtils.endsWith(sb.toString(), "/") ) sb.append('/');
			if ( StringUtils.startsWith(uriSegment,"/"))
				sb.append(uriSegment.substring(1));
			else
				sb.append(uriSegment);
			
			return new URL( sb.toString() ) ;
		} catch (MalformedURLException e) {
			throw new UriLocationUtilitiesImpl.URIFormatException(e);
		}
	}

	
	
	public URL getEndpointServerRootUrl() {
		return endpointServerRootUrl;
	}


	public URL getBaseEndpointUrl() {
		return endpointBaseUrl;
	}


	/**
	 * @return the data type to be sent on {@link #doPost(AuthValues)} etc.
	 */
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	
	protected <RsType> ResponseEntity<RsType> doGet(final AuthValues authValues, final String uri,Class<RsType> responseType) throws WorkflowException {
		
		return exchange(HttpMethod.GET,authValues,uri,null,responseType);
	}


	protected <RsType> ResponseEntity<RsType> doDelete(final AuthValues authValues, final String uri,Class<RsType> responseType) throws WorkflowException {

		return exchange(HttpMethod.DELETE,authValues,uri,null,responseType);
	}


	/**
	 * POSTs data of type <code>ENTITY</code> to the end point defined on the url.
	 */
	protected <RxType,RsType> ResponseEntity<RsType> doPost(final AuthValues authValues, 
															final String uri,
															final RxType body ,
															final Class<RsType> responseType) throws WorkflowException {
		
		return exchange(HttpMethod.POST,authValues,uri,body,responseType);
	}
	
	protected <RxType,RsType> ResponseEntity<RsType> doPost(final UserValues userValues, 
															final String uri,
															final RxType body ,
															final Class<RsType> responseType) throws WorkflowException {

		return exchange(HttpMethod.POST,userValues,uri,body,responseType);
	}
	
	/**
	 * Sends data of type <code>ENTITY</code> to the end point defined on the url.
	 * @param uriSegment the destination.
	 * @throws ClientErrorWorkflowException
	 * @param ENTITY is the class sent and retrieved.
	 */
	private <RxType,RsType> ResponseEntity<RsType> exchange( final HttpMethod method,
														     final AuthValues authToken, 
														     final String uriSegment,
														     RxType body ,
														     Class<RsType> responseType) throws WorkflowException {
		
		URL url = createEndpointUrl(uriSegment);
		
		if ( LOG.isDebugEnabled() && (body != null))
		{
			try {
				LOG.debug(String.format("%s to %s: %s", method.toString(), url , mapper.writeValueAsString(body) ));
			} catch (JsonProcessingException ex) {
				LOG.debug("Error, creating JSON",ex);
			}
		}
		
		try {
			
			HttpHeaders headers = authToken.createHttpHeaders();
			
			HttpEntity<RxType> entity = new HttpEntity<RxType>(body,headers);

			ResponseEntity<RsType> response = getRestTemplate().exchange(url.toString(),method,entity,responseType);

			LOG.info( String.format("%d from %s to '%s'", response.getStatusCodeValue(), method , url ) );
			return response;
			
		}
		// Error thrown when a 4xx is returned.
		catch(HttpClientErrorException ex){
			LOG.error( String.format("%s to '%s' error %s\nRESP: %s", method , url , ex.getMessage() , ex.getResponseBodyAsString() ) );

			if ( APPLICATION_JSON_UTF8.isCompatibleWith(ex.getResponseHeaders().getContentType()) )
			{
				// We tried to do something we were not allowed to do & there is a response.
				// Can we extract the error from the response	
				JsonNode responseBodyAsJson;
				try {
					responseBodyAsJson = mapper.readTree(ex.getResponseBodyAsString());
				} catch (Exception e) {
					// fall through
					responseBodyAsJson = null ;
				}
				// If the Response JSO is a standard API format then there will be a message node.
				if ( responseBodyAsJson.has("message") )
				{
					// Use the reason returned as the new error
					throw new UnableToPerformWorkflowException ( String.format( "Service '%s' reported %s." , getServiceName() , responseBodyAsJson.get("message").asText()));					
				}
			}
			
			return new ResponseEntity<RsType>(ex.getResponseHeaders(),ex.getStatusCode() );
		}
		catch (RestClientException ex) {
			throw new BadUpstreamClientWorkflowException(ex.getMessage(),ex);
		}
	}

	private Class<?> getServiceClass() {
		@SuppressWarnings("unchecked")
		Class<T> persistentClass = (Class<T>)
				   ((ParameterizedType)getClass().getGenericSuperclass())
				      .getActualTypeArguments()[0];
		return persistentClass;
	}
	
	private String getServiceName() {
		return getServiceClass().getSimpleName();
	}


	@Override
    public Health health() {
		return DownstreamHealthIndicator.fetchHealth(getRestTemplate(), getEndpointServerRootUrl());
	}
	
   @Override
   public void contribute(Builder builder) {	   
	   detailBuilder.withDetail(this,builder);
    }




}
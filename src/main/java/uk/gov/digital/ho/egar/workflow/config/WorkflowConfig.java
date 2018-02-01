package uk.gov.digital.ho.egar.workflow.config;

import java.util.Arrays;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.client.RestTemplate;

import uk.gov.digital.ho.egar.shared.auth.api.filter.config.FilterConfig;

@Configuration
@ComponentScan(basePackages = {"uk.gov.digital.ho.egar.shared.util, uk.gov.digital.ho.egar.shared.auth"}) 
public class WorkflowConfig {
	private static final Logger logger = LoggerFactory.getLogger(WorkflowConfig.class);

	@Bean
	FilterConfig filterConfig() {
		logger.debug("Initialising Filter Config");
		FilterConfig config = new FilterConfig();
		config.addPaths(Arrays.asList(
				new RegexRequestMatcher("\\/api\\/v1*.*", HttpMethod.POST.toString()),
				new RegexRequestMatcher("\\/api\\/v1*.*", HttpMethod.GET.toString()),
				new RegexRequestMatcher("\\/api\\/v1*.*", HttpMethod.DELETE.toString())));
		return config;
	}
	
	@Bean
	public RestTemplate restTemplate() {

		final RestTemplate restTemplate = new RestTemplate();
		final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		final HttpClient httpClient = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build();
		factory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(factory);

	    return restTemplate;
	}
}
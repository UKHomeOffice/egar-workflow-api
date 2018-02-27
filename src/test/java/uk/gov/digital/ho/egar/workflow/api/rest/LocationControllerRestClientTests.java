package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Assert;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;

/**
 * Runs tests against a service hosted locally.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties
		={
				"eureka.client.enabled=false",
				"spring.cloud.config.discovery.enabled=false",
				"spring.profiles.active=dev,"
										+ "mock-gar,"
										+ "mock-person,"
										+ "mock-file,"
										+ "mock-submission,"
										+ "mock-gar-search,"
										+ "mock-aircraft"
})
@AutoConfigureMockMvc
@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
public class LocationControllerRestClientTests extends LocationControllerTest {


	@Autowired
	private WorkflowPropertiesConfig workflowUrlConfig ;
	
	@Before
	public void shouldHaveLocationRestEndPoint() throws Exception
	{
		URL locationServerApi = new URL(workflowUrlConfig.getLocationApiURL());
		URL locationServerHealth = new URL(locationServerApi,"/health");
		
		HttpURLConnection connection ;
		
		try {
			connection = (HttpURLConnection)locationServerHealth.openConnection();
			connection.connect();
			
			assertThat(connection.getResponseCode())
				.as("Location Service is not online.")
				.isEqualTo(200);
			
		} catch (ConnectException ex) {
			
			Assert.fail("Location Service is not online:" + ex.getMessage() );
		}
		finally
		{
		}
	}
	
}

package uk.gov.digital.ho.egar.workflow.api.rest.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
		={
				"eureka.client.enabled=false",
				"spring.cloud.config.discovery.enabled=false",
				"spring.profiles.active=dev,"
										+ "mock-location,"
										+ "mock-person,"
										+ "mock-file,"
										+ "mock-submission,"
										+ "mock-aircraft,"
										+ "mock-attribute "
})
@AutoConfigureMockMvc
@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
public class RestClientBulkGarSummaryTests extends BulkGarSummaryTests{

	@Autowired
	private WorkflowPropertiesConfig workflowUrlConfig ;
	
	@Before
	public void shouldHaveLocationRestEndPoint() throws Exception
	{
		URL garServerApi = new URL(workflowUrlConfig.getGarApiURL());
		URL garServerHealth = new URL(garServerApi,"/health");
		
		HttpURLConnection connection ;
		
		try {
			connection = (HttpURLConnection)garServerHealth.openConnection();
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

package uk.gov.digital.ho.egar.workflow.api.rest;

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

import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;

import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
		={
				"eureka.client.enabled=false",
				"spring.cloud.config.discovery.enabled=false",
				"spring.profiles.active=dev,"
										+ "mock-gar,"
										+ "mock-location,"
										+ "mock-person,"
										+ "mock-file,"
										+ "mock-aircraft,"
										+ "mock-attribute ",
		"workflow.cancellation.arrival.threshold.time.s=7200",
		"workflow.submission.arrival.cutoff.time.s=-10800"
})
@AutoConfigureMockMvc
@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
public class SubmissionControllerRestClientTests extends SubmissionControllerTest {

	@Autowired
	private WorkflowPropertiesConfig workflowUrlConfig ;
	
	@Before
	public void shouldHaveLocationRestEndPoint() throws Exception
	{
		URL submissionServerApi = new URL(workflowUrlConfig.getSubmissionApiURL());
		URL submissionServerHealth = new URL(submissionServerApi,"/health");
		
		HttpURLConnection connection ;
		
		try {
			connection = (HttpURLConnection)submissionServerHealth.openConnection();
			connection.connect();
			
			assertThat(connection.getResponseCode())
				.as("Submission Service is not online.")
				.isEqualTo(200);
			
		} catch (ConnectException ex) {
			
			Assert.fail("Submission Service is not online:" + ex.getMessage() );
		}
		finally
		{
		}
	}
}

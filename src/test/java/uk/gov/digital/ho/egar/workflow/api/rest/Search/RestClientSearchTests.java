package uk.gov.digital.ho.egar.workflow.api.rest.Search;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
		={
				"eureka.client.enabled=false",
				"spring.cloud.config.discovery.enabled=false",
				"test.userest=true",
				"spring.profiles.active="
										+ "mock-gar,"
										+ "mock-location,"
										+ "mock-person,"
										+ "mock-file,"
										+ "mock-submission,"
										+ "mock-aircraft,"
										+ "mock-attribute "
})
@AutoConfigureMockMvc
@Ignore
@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
public class RestClientSearchTests extends SearchTests {

}

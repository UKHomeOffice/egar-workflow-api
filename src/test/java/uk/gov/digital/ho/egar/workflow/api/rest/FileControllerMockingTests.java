package uk.gov.digital.ho.egar.workflow.api.rest;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
		={
				"eureka.client.enabled=false",
				"spring.cloud.config.discovery.enabled=false",
				"spring.profiles.active=dev,"
						+ "mock-gar,"
						+ "mock-location,"
						+ "mock-person,"
						+ "mock-submission,"
						+ "mock-aircraft,"
						+ "mock-file,"
						+ "mock-gar-search"
})
@AutoConfigureMockMvc
public class FileControllerMockingTests extends FileControllerTest {

}

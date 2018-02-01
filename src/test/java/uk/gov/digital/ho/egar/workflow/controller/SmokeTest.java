package uk.gov.digital.ho.egar.workflow.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.digital.ho.egar.workflow.WorkflowApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
				+ "mock-submission,"
				+ "mock-aircraft,"
				+ "mock-attribute "
})
@AutoConfigureMockMvc
public class SmokeTest {

    static final MediaType TEXT_HTML_UTF8 = MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");
    static final MediaType APPLICATION_VND_JSON_UTF8 = MediaType.valueOf("application/vnd.spring-boot.actuator.v1+json;charset=UTF-8");

    @Autowired
    private WorkflowApplication app;
    @Autowired
    private MockMvc mockMvc;


	@Test
	public void contextLoads() {
	    assertThat(app).isNotNull();
	}

	/**
	 * SpringBoot heath endpoint
	 * @throws Exception
	 */
    @Test
    public void shouldHaveHealthEndpoint() throws Exception {
        this.mockMvc
                .perform(get("/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_VND_JSON_UTF8))
                .andExpect(jsonPath("$.status", is("UP")));
    }
    
    /**
     * Liveness - Is the current running state and health of the application service
     * A /healthz endpoint for the liveness Probe
	 * https://github.com/UKHomeOffice/technical-service-requirements/blob/master/docs/monitoring_metrics.md
     * @throws Exception
     */
    @Test
    public void shouldHaveHOLivenessEndpoint() throws Exception {
        this.mockMvc
                .perform(get("/healthz"))
                .andDo(print())
                .andExpect(status().isOk());
    }



    
    /**
     * Readiness - This is when the service is ready to be consumed. 
     * An example is performing some schema migrations or waiting for dependent services to be ready before your service can be ready. 
     * We wouldn't want the service to be consumed until it is ready to be so.
     * A /healthz endpoint for the liveness Probe
	 * https://github.com/UKHomeOffice/technical-service-requirements/blob/master/docs/monitoring_metrics.md
     * @throws Exception
     */
    @Test
    public void shouldHaveHOReadinessEndpoint() throws Exception {
        this.mockMvc
                .perform(get("/readiness"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    
    /** 
     * Readiness - This is when the service is ready to be consumed. 
     * An example is performing some schema migrations or waiting for dependent services to be ready before your service can be ready. 
     * We wouldn't want the service to be consumed until it is ready to be so.
     * A /healthz endpoint for the liveness Probe
	 * https://github.com/UKHomeOffice/technical-service-requirements/blob/master/docs/monitoring_metrics.md
     * @throws Exception
     */
    @Test
    public void shouldHaveHOMetricsEndpoint() throws Exception {
        this.mockMvc
                .perform(get("/metrics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_VND_JSON_UTF8))
                .andExpect(jsonPath("$.mem").exists());
    }
    
    @Test
    public void shouldHaveHomePage() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_UTF8));
    }

    
    @Test
    public void shouldHaveSwagger2Endpoint() throws Exception {
        this.mockMvc
                .perform(get("/v2/api-docs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.swagger", is("2.0")));
    }

    @Test
    public void shouldHaveSwagger2Page() throws Exception {
        this.mockMvc
                .perform(get("/swagger-ui.html"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE));
    }

}

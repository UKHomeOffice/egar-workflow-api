package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;

import javax.annotation.PostConstruct;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static uk.co.civica.microservice.util.testing.utils.FileReaderUtils.readFileAsString;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AIRCRAFT_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;

public abstract class AircraftControllerTest {

	@Rule
	public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();
	
	@Autowired
	private WorkflowApplication app;
	@Autowired
	private MockMvc mockMvc;
	
	private TestDependacies retriever ;
	
	@PostConstruct
	private void init()
	{
		retriever = new TestDependacies(mockMvc); 
	}
	
	@Test
	public void contextLoads() {
		assertThat(app).isNotNull();
	}
	
	
	private void addAircraftDataToGar(String garUuid) throws Exception {
		
        this.mockMvc
        .perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
        		.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
                .contentType(APPLICATION_JSON_UTF8_VALUE)	
                .content(TestDependacies.aircraftTestData()))
        .andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location", AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid)));
        JsonNode aircraftResponse = retriever.getContentAsJsonNode(USER_UUID,AUTH, AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid));
        assertThat(aircraftResponse).isNotNull();
		assertThat(aircraftResponse.has("aircraft")).isTrue();
		assertEquals(aircraftResponse.get("aircraft").get("base").asText(), 		("123D"));
		assertEquals(aircraftResponse.get("aircraft").get("registration").asText(), ("REG"));
		assertEquals(aircraftResponse.get("aircraft").get("taxesPaid").asText(), 	("false"));
		assertEquals(aircraftResponse.get("aircraft").get("type").asText(), 		("Fighter"));
        
	}
	//----------------------------------------
	/*
	 * GET
	 */
	//----------------------------------------
	/*
	 * Retrieving GAR from end point
	 */
	
	@Test
	public void retrieveAircraftDetailsOfExistingGAR() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
				.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,  AUTH))
				.andDo(print())
		// THEN
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}
	
	@Test
	public void noMatchOnRetrieveAircraftDetailsOfExistingGAR() throws Exception{	
		// WITH
				final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		//WHEN
		this.mockMvc
				.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,  AUTH))
				.andDo(print())
		// THEN
				.andExpect(status().isBadRequest());
				
	}	
	
	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveAircraftDetailsOfExistingGAR() throws Exception{	
		this.mockMvc
				.perform(get(String.format("/api/v1/WF/GARs/%s/aircraft/",retriever.retrieveGarUUID(USER_UUID, AUTH)))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,  AUTH))
				.andDo(print())
				.andExpect(status().isUnauthorized());
				
	}	
	/*
	 * Checking GAR has an assigned GAR UUID
	 */
	
	@Test
	public void aircraftDetailsContainGarUUIDContent() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
			.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH))
			.andDo(print())
		//THEN
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.gar_uuid").exists())
            .andExpect(jsonPath("$.gar_uuid", matchesRegex(REGEX_UUID)));
	}
	/*
	 * Checking GAR has an assigned user UUID
	 */
	
	@Test
	public void aircraftDetailsContainUserUUIDContent() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
			.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH))
			.andDo(print())
		// THEN	
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.user_uuid").exists())
            .andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));
	}
	/*
	 * Checking GAR has required aircraft details
	 */
	@Test
	public void aircraftDetailsContainRegistrationContent() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
			.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH))
			.andDo(print())
		// THEN	
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.aircraft.registration").exists())
            .andExpect(jsonPath("$.aircraft.registration").isString());
	}
	
	@Test
	public void aircraftDetailsContainTypeContent() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
			.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH))
			.andDo(print())
		// THEN	
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.aircraft.type").exists())
            .andExpect(jsonPath("$.aircraft.type").isString());
	}
	
	@Test
	public void aircraftDetailsContainBaseContent() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
			.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH))
			.andDo(print())
		// THEN
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
			.andExpect(jsonPath("$.aircraft.base").exists())
            .andExpect(jsonPath("$.aircraft.base").isString());
	}

	@Test
	public void aircraftDetailsContainTaxesPaidBoolean() throws Exception{
		// WITH
		final String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAircraftDataToGar(garUuid);
		// WHEN
		this.mockMvc
			.perform(get(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH))
			.andDo(print())
		// THEN
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.aircraft.taxesPaid").exists())
			.andExpect(jsonPath("$.aircraft.taxesPaid").isBoolean());
	}	
	
	//----------------------------------------
	/*
	 * POST
	 */
	//----------------------------------------
	/*
	 * Adding/Amending aircraft details
	 */
	
	@Test
	public void addingAircraftDetails() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
        this.mockMvc
        .perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
        		.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
                .contentType(APPLICATION_JSON_UTF8_VALUE)	
                .content(TestDependacies.aircraftTestData()))
        .andDo(print())
        // THEN
        .andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location", AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid)));
        JsonNode aircraftResponse = retriever.getContentAsJsonNode(USER_UUID,AUTH, AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid));
        assertThat(aircraftResponse).isNotNull();
		assertThat(aircraftResponse.has("aircraft")).isTrue();
		assertEquals(aircraftResponse.get("aircraft").get("base").asText(), ("123D"));
		assertEquals(aircraftResponse.get("aircraft").get("registration").asText(), ("REG"));
		assertEquals(aircraftResponse.get("aircraft").get("taxesPaid").asText(), ("false"));
		assertEquals(aircraftResponse.get("aircraft").get("type").asText(), ("Fighter"));

	}
		
	@Test
	public void noMatchAddingAircraftDetailsToIMaginaryGar() throws Exception{
		// WITH
		final String aircraft = readFileAsString("data/AircraftTestData/AircraftTestData.json");
		final String garUuid = UUID.randomUUID().toString();
		// WHEN
		this.mockMvc
			.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(aircraft))
		// THEN
			.andExpect(status().isBadRequest());
	}
	
	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedAddingAircraftDetails() throws Exception{
		this.mockMvc
			.perform(post(String.format("/api/v1/WF/GARs/%s/aircraft/",retriever.retrieveGarUUID(USER_UUID, AUTH)))
					.contentType(APPLICATION_JSON_UTF8_VALUE))
			.andExpect(status().isUnauthorized());
	}
	@Test
	public void forbiddenAddingAircraftDetails() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL));
		this.mockMvc
			.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,   AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.aircraftTestData()))
		// THEN
			.andExpect(status().isForbidden());
	}



}
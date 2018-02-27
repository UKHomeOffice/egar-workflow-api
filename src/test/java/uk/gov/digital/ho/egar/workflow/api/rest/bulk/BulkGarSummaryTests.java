package uk.gov.digital.ho.egar.workflow.api.rest.bulk;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AIRCRAFT_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.GAR_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.ATTRIBUTE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LOCATION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LocationType.ICAO;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CAPTAIN;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;

public abstract class BulkGarSummaryTests {
	
	@Rule
	public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ObjectMapper mapper;

	private TestDependacies retriever ;

	@PostConstruct
	private void init()
	{
		retriever = new TestDependacies(mockMvc); 
	}

	@Test
	public void bulkGetGars() throws Exception{
		List<UUID> garList =  new ArrayList<>();
		for(int i=0; i< 3 ;i++){
			String garUuid= retriever.createAGar(USER_UUID, AUTH);
			
			garList.add(UUID.fromString(garUuid));
			// WITH
			// WHEN
			// ADD Aircraft
			this.mockMvc
			.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER, AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.aircraftTestData()))
			.andExpect(status().isSeeOther());
			// ADD Arrival 
			this.mockMvc
			.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
					.replace("{option}", "arr"))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER, AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.arrivalTestData(ICAO)))
			.andExpect(status().isSeeOther());
			// ADD Departure 
			this.mockMvc
			.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
					.replace("{option}", "dept"))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER, AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.departureTestData(ICAO)))
			.andExpect(status().isSeeOther());
			// ADD Captain
			this.mockMvc
			.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,   AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.personTestData(CAPTAIN)))
			.andExpect(status().isSeeOther());
			// ADD Attributes
			this.mockMvc
			.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,   AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.attributeTestData()))
			.andExpect(status().isSeeOther());
		}
		
		GarListResponse gars = new GarListResponse();
		gars.setGarIds(garList);
		
		String simpleJSON = mapper.writeValueAsString(gars);
		
		this.mockMvc
		.perform(post(GAR_SERVICE_NAME + "summaries")
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(simpleJSON))
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gars").exists())
		.andExpect(jsonPath("$.gars").isArray())
		.andExpect(jsonPath("$.gars[0].gar_uuid").exists())
		.andExpect(jsonPath("$.gars[0].gar_uuid", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.gars[0].user_uuid").exists())
		.andExpect(jsonPath("$.gars[0].user_uuid", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.gars[0].aircraft").exists())
		.andExpect(jsonPath("$.gars[0].location").exists())
		.andExpect(jsonPath("$.gars[0].people").exists())
		.andExpect(jsonPath("$.gars[0].attributes").exists())
		;
		
		
	}
	
	@Test
	public void emptyGarTest() throws Exception{
		List<UUID> garList =  new ArrayList<>();
		for(int i=0; i < 3 ;i++){
			String garUuid= retriever.createAGar(USER_UUID, AUTH);
			
			garList.add(UUID.fromString(garUuid));
		}
		GarListResponse gars = new GarListResponse();
		gars.setGarIds(garList);
		String simpleJSON = mapper.writeValueAsString(gars);
		this.mockMvc
		.perform(post(GAR_SERVICE_NAME + "summaries")
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(simpleJSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gars").exists())
		.andExpect(jsonPath("$.gars").isArray())
		.andExpect(jsonPath("$.gars[0].gar_uuid").exists())
		.andExpect(jsonPath("$.gars[0].gar_uuid", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.gars[0].user_uuid").exists())
		.andExpect(jsonPath("$.gars[0].user_uuid", matchesRegex(REGEX_UUID)));
	}


}

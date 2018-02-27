package uk.gov.digital.ho.egar.workflow.api.rest.Search;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AIRCRAFT_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.GAR_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LOCATION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CREW;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SEARCH_SERVICE_NAME;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies;


public abstract class SearchTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ObjectMapper mapper;

	private TestDependacies retriever;

	@PostConstruct
	private void init()
	{
		retriever = new TestDependacies(mockMvc); 
	}
	@Test
	public void searchPeopleSuccess() throws Exception{
		UUID firstUser = UUID.randomUUID();
		UUID secondUser = UUID.randomUUID();
		// post person for different user
		String otherGarUuid= retriever.createAGar(firstUser, AUTH);
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", otherGarUuid))
				.header(USERID_HEADER, firstUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)));
		// Post several people for user to be tested
		String garUuid= retriever.createAGar(secondUser, AUTH);
		for(int i=0; i< 4 ;i++){
			this.mockMvc
			.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, secondUser)
					.header(AUTH_HEADER,   AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.personTestData(CREW)));
		}

		this.mockMvc
		.perform(get(SEARCH_SERVICE_NAME + "/persons/")
				.header(USERID_HEADER, secondUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.person_uuids").exists())
		.andExpect(jsonPath("$.person_uuids").isArray());

		JsonNode response = retriever.getContentAsJsonNode(secondUser, AUTH, SEARCH_SERVICE_NAME + "/persons/");
		assertEquals("Expected 4 people",  4, response.get("person_uuids").size());
	}
	//@Test
	public void emptySearchGarSuccessForAUserReturnsList() throws Exception{
		// WITH
		UUID thisUser  = UUID.randomUUID();
		UUID otherUser = UUID.randomUUID();
		// creat gars
		String garUuid1 = retriever.createAGar(thisUser, AUTH);
		String garUuid2 = retriever.createAGar(thisUser, AUTH);
		String garUuid3 = retriever.createAGar(thisUser, AUTH);
		String otherUserGAR = retriever.createAGar(otherUser, AUTH);
		// WHEN
		// ADD Aircraft to gars
		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid1))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft1")))
		.andExpect(status().isSeeOther());

		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid2))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft1")))
		.andExpect(status().isSeeOther());

		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid3))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft2")))
		.andExpect(status().isSeeOther());

		// ADD Aircraft for different user
		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", otherUserGAR))
				.header(USERID_HEADER, otherUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft1")))
		.andExpect(status().isSeeOther());
		//THEN		
		// Search
		this.mockMvc
		.perform(get(GAR_SERVICE_NAME)
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				)
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuids").exists())
		.andExpect(jsonPath("$.gar_uuids").isArray());

		JsonNode response = retriever.getContentAsJsonNode(thisUser, AUTH, GAR_SERVICE_NAME);
		assertEquals(response.get("gar_uuids").size(), 3);
	}
	
	//@Test
	public void searchGarAircraftRegSuccessForAUser() throws Exception{
		// WITH
		UUID thisUser  = UUID.randomUUID();
		UUID otherUser = UUID.randomUUID();
		// creat gars
		String garUuid1 = retriever.createAGar(thisUser, AUTH);
		String garUuid2 = retriever.createAGar(thisUser, AUTH);
		String garUuid3 = retriever.createAGar(thisUser, AUTH);
		String otherUserGAR = retriever.createAGar(otherUser, AUTH);
		// WHEN
		// ADD Aircraft  and location to multiple gars
		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid1))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft1")))
		.andExpect(status().isSeeOther());

		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid2))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft1")))
		.andExpect(status().isSeeOther());

		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid3)
				.replace("{option}", "dept"))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("IcaoDep2")))
		.andExpect(status().isSeeOther());

		// ADD Aircraft for different user
		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", otherUserGAR))
				.header(USERID_HEADER, otherUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("Aircraft1")))
		.andExpect(status().isSeeOther());
		//THEN		
		// Search
		this.mockMvc
		.perform(get(SEARCH_SERVICE_NAME +"/GARs/?search_criteria=REG")
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				)
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuids").exists())
		.andExpect(jsonPath("$.gar_uuids").isArray());

		JsonNode response = retriever.getContentAsJsonNode(thisUser, AUTH, SEARCH_SERVICE_NAME +"/GARs/?search_criteria=REG");
		assertEquals(response.get("gar_uuids").size(), 2);
	}

	//@Test
	public void searchGarICAOSuccessForAUser() throws Exception{
		// WITH
		UUID thisUser  = UUID.randomUUID();
		UUID otherUser = UUID.randomUUID();
		// creat gars
		String garUuid1 = retriever.createAGar(thisUser, AUTH);
		String garUuid2 = retriever.createAGar(thisUser, AUTH);
		String garUuid3 = retriever.createAGar(thisUser, AUTH);
		String otherUserGAR = retriever.createAGar(otherUser, AUTH);
		// ADD Departure to gars
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid1)
				.replace("{option}", "dept"))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("IcaoDep1")))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid2)
				.replace("{option}", "dept"))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("IcaoDep1")))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid3)
				.replace("{option}", "dept"))
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("IcaoDep2")))
		.andExpect(status().isSeeOther());

		// ADD departure for other user
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", otherUserGAR)
				.replace("{option}", "dept"))
				.header(USERID_HEADER, otherUser)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.searchTestData("IcaoDep1")))
		.andExpect(status().isSeeOther());
		//THEN		
		// Search
		this.mockMvc
		.perform(get(GAR_SERVICE_NAME +"?search_criteria=EBBT")
				.header(USERID_HEADER, thisUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				)
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuids").exists())
		.andExpect(jsonPath("$.gar_uuids").isArray());

		JsonNode response = retriever.getContentAsJsonNode(thisUser, AUTH, SEARCH_SERVICE_NAME +"/GARs/?search_criteria=EBBT");
		assertEquals(response.get("gar_uuids").size(), 2);
	}
	
	
	
}

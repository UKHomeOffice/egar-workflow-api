package uk.gov.digital.ho.egar.workflow.api.rest.Search;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CREW;

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
		//Post several people for user to be tested
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
		.perform(get("/api/v1/WF/Persons/")
				.header(USERID_HEADER, secondUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.person_uuids").exists())
		.andExpect(jsonPath("$.person_uuids").isArray());

		JsonNode response = retriever.getContentAsJsonNode(secondUser, AUTH, "/api/v1/WF/Persons/");
		assertEquals(response.get("person_uuids").size(), 4);
	}
	@Test
	public void searchGarSuccess() throws Exception{
		UUID firstUser = UUID.randomUUID();
		UUID secondUser = UUID.randomUUID();
		retriever.createAGar(firstUser, AUTH);
		for(int i=0; i< 4 ;i++){
			retriever.createAGar(secondUser, AUTH);
		}
		this.mockMvc
		.perform(get("/api/v1/WF/GARs/")
				.header(USERID_HEADER, secondUser)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuids").exists())
		.andExpect(jsonPath("$.gar_uuids").isArray());
		
		JsonNode response = retriever.getContentAsJsonNode(secondUser, AUTH, "/api/v1/WF/GARs/");
		assertEquals(response.get("gar_uuids").size(), 4);
	}
}

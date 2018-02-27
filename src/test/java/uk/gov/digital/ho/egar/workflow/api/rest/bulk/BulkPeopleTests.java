package uk.gov.digital.ho.egar.workflow.api.rest.bulk;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_COUNTRY_CODE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_DOB;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_GENDER;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CREW;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.EMPTY_PASSENGER;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;

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
						+ "mock-attribute,"
						+ "mock-file",
})
@AutoConfigureMockMvc
public class BulkPeopleTests {
	
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
	public void bulkGetPeople() throws Exception{
		List<UUID> peopleList =  new ArrayList<>();
		
		for(int i=0; i< 4 ;i++){
			String garUuid= retriever.createAGar(USER_UUID, AUTH);

			MvcResult result =
					this.mockMvc
					.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
							.header(USERID_HEADER, USER_UUID)
							.header(AUTH_HEADER,   AUTH)
							.contentType(APPLICATION_JSON_UTF8_VALUE)
							.content(TestDependacies.personTestData(CREW)))
					.andExpect(status().isSeeOther())
					.andExpect(header().string("Location", not(isNull())))
					.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
					.andReturn();
			String personUri = result.getResponse().getHeader("Location");
			String uuid = personUri.substring(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(), personUri.length()-1);
			
			peopleList.add(UUID.fromString(uuid));
			
		}
		
		PersonUUIDList people = new PersonUUIDList();
		people.setPersonUuids(peopleList);

		String simpleJSON = mapper.writeValueAsString(people);

		this.mockMvc
		.perform(post("/api/v1/WF/summaries/persons/")
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(simpleJSON))
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people").exists())
		.andExpect(jsonPath("$.people").isArray())
		.andExpect(jsonPath("$.people[0].person_uuid").exists())
		.andExpect(jsonPath("$.people[0].person_uuid", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.people[0].details.given_name").exists())
		.andExpect(jsonPath("$.people[0].details.given_name").isString())
		.andExpect(jsonPath("$.people[0].details.family_name").exists())
		.andExpect(jsonPath("$.people[0].details.family_name").isString())
		.andExpect(jsonPath("$.people[0].details.gender").exists())
		.andExpect(jsonPath("$.people[0].details.gender",matchesRegex(REGEX_GENDER)))
		.andExpect(jsonPath("$.people[0].details.address").exists())
		.andExpect(jsonPath("$.people[0].details.address").isString())
		.andExpect(jsonPath("$.people[0].details.dob").exists())
		.andExpect(jsonPath("$.people[0].details.dob", matchesRegex(REGEX_DOB)))
		.andExpect(jsonPath("$.people[0].details.place").exists())
		.andExpect(jsonPath("$.people[0].details.place").isString())
		.andExpect(jsonPath("$.people[0].details.nationality").exists())
		.andExpect(jsonPath("$.people[0].details.nationality").isString())
		.andExpect(jsonPath("$.people[0].details.document_type").exists())
		.andExpect(jsonPath("$.people[0].details.document_type").isString())
		.andExpect(jsonPath("$.people[0].details.document_no").exists())
		.andExpect(jsonPath("$.people[0].details.document_no").isString())
		.andExpect(jsonPath("$.people[0].details.document_expiryDate").exists())
		.andExpect(jsonPath("$.people[0].details.document_expiryDate", matchesRegex(REGEX_DOB)))
		.andExpect(jsonPath("$.people[0].details.document_issuingCountry").exists())
		.andExpect(jsonPath("$.people[0].details.document_issuingCountry",matchesRegex(REGEX_COUNTRY_CODE)))
		;
		
	}
	
	@Test
	public void bulkGetPeopleEmptyPassenger() throws Exception{
		List<UUID> peopleList =  new ArrayList<>();
		
		for(int i=0; i< 4 ;i++){
			String garUuid= retriever.createAGar(USER_UUID, AUTH);

			MvcResult result =
					this.mockMvc
					.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
							.header(USERID_HEADER, USER_UUID)
							.header(AUTH_HEADER,   AUTH)
							.contentType(APPLICATION_JSON_UTF8_VALUE)
							.content(TestDependacies.personTestData(EMPTY_PASSENGER)))
					.andExpect(status().isSeeOther())
					.andExpect(header().string("Location", not(isNull())))
					.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
					.andReturn();
			String personUri = result.getResponse().getHeader("Location");
			String uuid = personUri.substring(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(), personUri.length()-1);
			
			peopleList.add(UUID.fromString(uuid));
			
		}
		PersonUUIDList people = new PersonUUIDList();
		people.setPersonUuids(peopleList);

		String simpleJSON = mapper.writeValueAsString(people);
		this.mockMvc
		.perform(post("/api/v1/WF/summaries/persons/")
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER, AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(simpleJSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people").exists())
		.andExpect(jsonPath("$.people").isArray())
		.andExpect(jsonPath("$.people[0].person_uuid").exists())
		.andExpect(jsonPath("$.people[0].person_uuid", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.people[0].details").exists())
		;
	}

}

package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNull;
import static com.jayway.jsonassert.JsonAssert.with;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_COUNTRY_CODE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_DOB;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_GENDER;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_PERSON_TYPE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CAPTAIN;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CREW;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.PASSENGER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.INVALID;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import com.fasterxml.jackson.databind.JsonNode;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;

public abstract class PersonControllerTest{

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

	private static final String INDIVIDUAL_PERSON_SERVICE_NAME = PERSON_SERVICE_NAME + "{person_uuid}/";

	private void addCaptainToGar(String garUuid) throws Exception {

		MvcResult result =
				this.mockMvc
				.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.personTestData(CAPTAIN)))
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
				.andReturn();
		String captainUri = result.getResponse().getHeader("Location");
		String uuid = captainUri.substring(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(), captainUri.length()-1);
		assertTrue(uuid.matches(REGEX_UUID));
		JsonNode captainResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, captainUri);
		assertThat(captainResponse).isNotNull();
		assertThat(captainResponse.has("person")).isTrue();
		assertEquals(captainResponse.get("person").get("type").asText(), 									"CAPTAIN");
		assertEquals(captainResponse.get("person").get("details").get("address").asText(),					"76 ABC");
		assertEquals(captainResponse.get("person").get("details").get("dob").asText(), 						"2017-11-16");
		assertEquals(captainResponse.get("person").get("details").get("document_expiryDate").asText(), 		"2035-11-16");
		assertEquals(captainResponse.get("person").get("details").get("document_issuingCountry").asText(),	"UK");
		assertEquals(captainResponse.get("person").get("details").get("document_no").asText(), 				"3533DGDTW63G33");
		assertEquals(captainResponse.get("person").get("details").get("document_type").asText(), 			"PASSPORT");
		assertEquals(captainResponse.get("person").get("details").get("family_name").asText(), 				"Bloggs");
		assertEquals(captainResponse.get("person").get("details").get("gender").asText(), 					"MALE");
		assertEquals(captainResponse.get("person").get("details").get("given_name").asText(), 				"Jhon");
		assertEquals(captainResponse.get("person").get("details").get("nationality").asText(), 				"UK");
		assertEquals(captainResponse.get("person").get("details").get("place").asText(), 					"London");

	}	
	//----------------------------------------
	/*
	 * GET
	 */
	//----------------------------------------
	/*
	 * 	Retrieve list persons from end point
	 */
	@Test
	public void retrieveListOfPerson() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));	
	}
	@Test
	public void noMatchRetrieveListOfPerson() throws Exception{
		// WITH
		// WHEN
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isForbidden());	
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveListOfPerson() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/persons/",retriever.retrieveGarUUID(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());	
	}
	/*
	 * Checking person is assigned a GAR UUID
	 */
	@Test
	public void personsContainsGarUUIDContent() throws Exception{
		// WITH	
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuid").exists())
		.andExpect(jsonPath("$.gar_uuid", matchesRegex(REGEX_UUID)));
	}
	/*
	 * Checking person is assigned a user UUID
	 */
	@Test
	public void personsContainsUserUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.user_uuid").exists())
		.andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void personsContainsCaptainContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN	
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.captain").exists())
		.andExpect(jsonPath("$.people.captain", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void personsContainsCrewContent() throws Exception{
		// WITH
		String garUuid =retriever.retrieveGarUUID(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)));
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)));
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.crew").exists())
		.andExpect(jsonPath("$.people.crew").isArray())
		.andExpect(jsonPath("$.people.crew[0]", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void personsContainsPassengersContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(PASSENGER)));
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(PASSENGER)));
		this.mockMvc
		.perform(get(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.passengers").exists())
		.andExpect(jsonPath("$.people.passengers").isArray())
		.andExpect(jsonPath("$.people.passengers[0]", matchesRegex(REGEX_UUID)));
	}	
	//----------------------------------------
	/*
	 * Retrieve a person from existing GAR
	 */
	@Test
	public void retrievePersonFromExistingGAR() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));	
	}
	@Test
	public void noMatchRetrievePersonFromExistingGAR() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN	
		.andExpect(status().isBadRequest());	
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrievePersonFromExistingGAR() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/persons/%s/",retriever.retrieveGarUUID(USER_UUID, AUTH),retriever.retrieveCaptainUUID(USER_UUID, AUTH, null)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());	
	}
	/*
	 * Checking person is assigned a GAR UUID
	 */
	@Test
	public void personContainsGarUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// WHEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuid").exists())
		.andExpect(jsonPath("$.gar_uuid", matchesRegex(REGEX_UUID)));
	}	
	/*
	 * Checking person is assigned a user UUID
	 */
	@Test
	public void personContainsUserUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);

		// WHEN/THEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		//THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.user_uuid").exists())
		.andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));
	}

	/*
	 * Checking person is assigned a person UUID
	 */
	@Test
	public void personContainsPersonUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN	
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.person.person_uuid").exists())
		.andExpect(jsonPath("$.person.person_uuid", matchesRegex(REGEX_UUID)));
	}
	/*
	 * Checking person is assigned a type  
	 */
	@Test
	public void personContainsPersonTypeContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.person.type").exists())
		.andExpect(jsonPath("$.person.type", matchesRegex(REGEX_PERSON_TYPE)));
	}
	/*
	 * Checking person is assigned rest of details  
	 */

	@Test
	public void personContainsDetailsContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.person.details.given_name").exists())
		.andExpect(jsonPath("$.person.details.given_name").isString())
		.andExpect(jsonPath("$.person.details.family_name").exists())
		.andExpect(jsonPath("$.person.details.family_name").isString())
		.andExpect(jsonPath("$.person.details.gender").exists())
		.andExpect(jsonPath("$.person.details.gender",matchesRegex(REGEX_GENDER)))
		.andExpect(jsonPath("$.person.details.address").exists())
		.andExpect(jsonPath("$.person.details.address").isString())
		.andExpect(jsonPath("$.person.details.dob").exists())
		.andExpect(jsonPath("$.person.details.dob", matchesRegex(REGEX_DOB)))
		.andExpect(jsonPath("$.person.details.place").exists())
		.andExpect(jsonPath("$.person.details.place").isString())
		.andExpect(jsonPath("$.person.details.nationality").exists())
		.andExpect(jsonPath("$.person.details.nationality").isString())
		.andExpect(jsonPath("$.person.details.document_type").exists())
		.andExpect(jsonPath("$.person.details.document_type").isString())
		.andExpect(jsonPath("$.person.details.document_no").exists())
		.andExpect(jsonPath("$.person.details.document_no").isString())
		.andExpect(jsonPath("$.person.details.document_expiryDate").exists())
		.andExpect(jsonPath("$.person.details.document_expiryDate", matchesRegex(REGEX_DOB)))
		.andExpect(jsonPath("$.person.details.document_issuingCountry").exists())
		.andExpect(jsonPath("$.person.details.document_issuingCountry",matchesRegex(REGEX_COUNTRY_CODE)));

	}
	//----------------------------------------
	/*
	 * POST
	 */
	//----------------------------------------
	/*
	 * Adding a new person for existing GAR
	 */
	@Test
	public void addingPersonForExistingGAR() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		MvcResult result =
				// WHEN				
				this.mockMvc
				.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.personTestData(CAPTAIN)))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
				.andReturn();

		String captainUri = result.getResponse().getHeader("Location");
		String uuid = captainUri.substring(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(),captainUri.length()-1);

		assertTrue(uuid.matches(REGEX_UUID));

		JsonNode captainResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, captainUri);
		assertThat(captainResponse).isNotNull();
		assertThat(captainResponse.has("person")).isTrue();
		assertEquals(captainResponse.get("person").get("type").asText(), 									"CAPTAIN");
		assertEquals(captainResponse.get("person").get("details").get("address").asText(),					"76 ABC");
		assertEquals(captainResponse.get("person").get("details").get("dob").asText(), 						"2017-11-16");
		assertEquals(captainResponse.get("person").get("details").get("document_expiryDate").asText(), 		"2035-11-16");
		assertEquals(captainResponse.get("person").get("details").get("document_issuingCountry").asText(),	"UK");
		assertEquals(captainResponse.get("person").get("details").get("document_no").asText(), 				"3533DGDTW63G33");
		assertEquals(captainResponse.get("person").get("details").get("document_type").asText(), 			"PASSPORT");
		assertEquals(captainResponse.get("person").get("details").get("family_name").asText(), 				"Bloggs");
		assertEquals(captainResponse.get("person").get("details").get("gender").asText(), 					"MALE");
		assertEquals(captainResponse.get("person").get("details").get("given_name").asText(), 				"Jhon");
		assertEquals(captainResponse.get("person").get("details").get("nationality").asText(), 				"UK");
		assertEquals(captainResponse.get("person").get("details").get("place").asText(), 					"London");
	}

	@Test
	public void noMatchAddingPersonForImaginaryGAR() throws Exception{
		// WITH
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)))
		// THEN
		.andExpect(status().isForbidden());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedAddingPersonForExistingGAR() throws Exception{
		this.mockMvc
		.perform(post(String.format("/api/v1/WF/GARs/%s/persons/",retriever.retrieveGarUUID(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isUnauthorized());
	}
	@Test
	public void forbiddenAddingSecondCaptainForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)));
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)))
		// THEN
		.andExpect(status().isForbidden());
	}
	//----------------------------------------
	/*
	 * Updating a person for existing GAR
	 * @See EGAR-1007
	 */
	@Test
	public void updatingPersonForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)));

		// WITH
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);

		// WHEN
		this.mockMvc
		.perform(post(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}", personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)))
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location", INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}", personUUID) ));

		// THEN
		JsonNode personResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}", personUUID));
		assertThat(personResponse).isNotNull();
		assertThat(personResponse.has("person")).isTrue();
		assertEquals(personResponse.get("person").get("type").asText(), 									"CREW");
		assertEquals(personResponse.get("person").get("details").get("address").asText(), 					"76 ABC");
		assertEquals(personResponse.get("person").get("details").get("dob").asText(), 						"2017-11-16");
		assertEquals(personResponse.get("person").get("details").get("document_expiryDate").asText(), 		"2017-11-16");
		assertEquals(personResponse.get("person").get("details").get("document_issuingCountry").asText(), 	"UK");
		assertEquals(personResponse.get("person").get("details").get("document_no").asText(), 				"3533DGDTW63G33");
		assertEquals(personResponse.get("person").get("details").get("document_type").asText(), 			"PASSPORT");
		assertEquals(personResponse.get("person").get("details").get("family_name").asText(), 				"Bloggs");
		assertEquals(personResponse.get("person").get("details").get("gender").asText(), 					"MALE");
		assertEquals(personResponse.get("person").get("details").get("given_name").asText(), 				"Joe");
		assertEquals(personResponse.get("person").get("details").get("nationality").asText(), 				"UK");
		assertEquals(personResponse.get("person").get("details").get("place").asText(), 					"Bath");

	}
	@Test
	public void noMatchUpdatingImaginaryPersonForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)))
		// THEN
		.andExpect(status().isBadRequest());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedUpdatingPersonForExistingGAR() throws Exception{
		this.mockMvc
		.perform(post(String.format("/api/v1/WF/GARs/%s/persons/%s/",retriever.retrieveGarUUID(USER_UUID, AUTH),retriever.retrieveCaptainUUID(USER_UUID, AUTH, null)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isUnauthorized());
	}
	@Test
	public void forbiddenUpdatingPersonToCaptainWhenCaptainAlreadyExists() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)));
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)));

		this.mockMvc
		.perform(post(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}", retriever.retrieveCrewUUID(USER_UUID, AUTH, garUuid)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)))
		// THEN
		.andExpect(status().isForbidden());
	}

	@Test
	public void forbiddenAddingPersonForSubmittedGAR() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL));
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)))
		// THEN
		.andExpect(status().isForbidden());
	}

	@Test
	public void forbiddenUpdatingPersonForSubmittedGAR() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CREW)))
		// THEN
		.andExpect(status().isForbidden());

	}

	// THEN
	//----------------------------------------
	/*
	 * DELETE
	 */
	//----------------------------------------
	/*
	 * Delete a person from an existing GAR
	 */
	@Test
	public void deletePersonFromExistingGAR() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addCaptainToGar(garUuid);
		String personUUID =retriever.retrieveCaptainUUID(USER_UUID, AUTH, garUuid);
		this.mockMvc
		.perform(delete(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",personUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		// THEN
		.andExpect(status().isAccepted());
	}

	@Test
	public void noMatchDeletePersonFromExistingGAR() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(delete(INDIVIDUAL_PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{person_uuid}",UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		// THEN
		.andExpect(status().isBadRequest());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedDeletePersonFromExistingGAR() throws Exception{
		this.mockMvc
		.perform(delete(String.format("/api/v1/WF/GARs/%s/persons/%s/",retriever.retrieveGarUUID(USER_UUID, AUTH),retriever.retrieveCaptainUUID(USER_UUID, AUTH, null)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andExpect(status().isUnauthorized());
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	@Test
	public void successfullyAddExistingPersonToGar() throws Exception{
		// WITH
		//ADD person to gar for user
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		MvcResult result =
				this.mockMvc
				.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.personTestData(CAPTAIN)))
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
				.andReturn();
		String captainUri = result.getResponse().getHeader("Location");
		String uuid = captainUri.substring(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(), captainUri.length()-1);
		

		String json = "{\"person_uuid\": \"{UUID}\", \"type\": \"captain\"}";
		//WHEN
		// add same person to another gar
		String newGarUuid =retriever.createAGar(USER_UUID, AUTH);
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", newGarUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(json.replace("{UUID}", uuid)))
		// THEN
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", newGarUuid))))
		;
	}

	@Test
	public void BadRequestAddingPersonWithDetailsAndIdToGar() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		MvcResult result =
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(INVALID)))
		// THEN
		.andExpect(status().isBadRequest())
		.andReturn();
		
		String response = result.getResponse().getContentAsString();

	    with(response).assertThat("$.message[0]", is("personWithId: Person must only have one: UUID or Details"));
		
		;
	}

	@Test
	public void BadRequestAddingRandomIdToGar() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		String json = "{\"person_uuid\": \"{UUID}\", \"type\": \"CREW\"}";
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(json.replace("{UUID}", UUID.randomUUID().toString())))
		// THEN
		.andExpect(status().isBadRequest())
		;
	}

}

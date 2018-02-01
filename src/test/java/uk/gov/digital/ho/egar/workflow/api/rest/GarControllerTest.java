package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_ICAO_CODE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_COUNTRY_CODE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_CONTACT;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_DOB;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_GENDER;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_PERSON_TYPE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_SUBMISSION_TYPE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_ZULU_DATETIME;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AIRCRAFT_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.FILE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LOCATION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.GAR_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.GAR_RETRIEVE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.GAR_SUMMARY_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.ATTRIBUTE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LocationType.ICAO;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CAPTAIN;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CREW;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.PASSENGER;



import java.util.UUID;

import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;


import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;

public abstract class GarControllerTest  {

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

	private void addAGar() throws Exception {

		MvcResult result =
				this.mockMvc
				.perform(post(GAR_SERVICE_NAME)
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH))
				.andDo(print())
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull()) ))
				.andExpect(header().string("Location", startsWith(GAR_SERVICE_NAME)))
				.andReturn();

		String location = result.getResponse().getHeader("Location");
		String uuid = location.substring(GAR_SERVICE_NAME.length(), location.length()-1);

		assertTrue(uuid.matches(REGEX_UUID));
	}
	//----------------------------------------
	/*
	 * GET
	 */
	//---------------------------------------
	/*
	 * Retrieving list of GARs from end point
	 */	
	@Test
	public void retrieveListOfExistingGars() throws Exception{
		// WITH
		addAGar();
		// WHEN
		this.mockMvc
		.perform(get(GAR_SERVICE_NAME)
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))			
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));

	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrievefListOfGars() throws Exception{
		this.mockMvc
		.perform(get(GAR_SERVICE_NAME)
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	/*
	 * Checking GARs have GAR UUID
	 */
	@Test
	public void garsContainGarUUIDContent() throws Exception{
		// WITH
		addAGar();
		addAGar();
		// WHEN
		this.mockMvc
		.perform(get(GAR_SERVICE_NAME)
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN	
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuids").exists())
		.andExpect(jsonPath("$.gar_uuids").isArray())
		.andExpect(jsonPath("$.gar_uuids[0]", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.gar_uuids[1]", matchesRegex(REGEX_UUID)))
		;


	}
	//----------------------------------------
	/*
	 * Retrieving GAR from end point skeleton view
	 */	
	@Test
	public void retrieveExistingGar() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}
	@Test
	public void noMatchRetrieveOfImaginedGar() throws Exception{
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isBadRequest());			
	}


	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveOfExistingGar() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());			
	}
	/*
	 * Checking GAR has an assigned GAR UUID
	 */
	@Test
	public void garContainsGarUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
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
	 * Checking GAR has an assigned user UUID
	 */
	@Test
	public void garContainsUserUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.user_uuid").exists())
		.andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));	
	}
	/*
	 * Checking GAR has an assigned submission boolean
	 */
	@Test
	public void garContainsSubmissionUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.submission").exists())
		.andExpect(jsonPath("$.submission").isBoolean());	
	}
	/*
	 * Checking GAR has assigned other fields for skeleton view(aircraft, location uuid,
	 * people details, file details and attributes.)
	 */
	@Test
	public void garContainsAircraftUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.aircraft").exists())
		.andExpect(jsonPath("$.aircraft").isBoolean());
	}
	@Test
	public void garContainsLocationUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(ICAO)))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.location_uuids").exists())
		.andExpect(jsonPath("$.location_uuids").isArray())
		.andExpect(jsonPath("$.location_uuids[0]", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.location_uuids[1]", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void garContainsCaptainContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.personTestData(CAPTAIN)));
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
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
	public void garContainsCrewContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
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
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.crew").exists())
		.andExpect(jsonPath("$.people.crew").isArray())
		.andExpect(jsonPath("$.people.crew[0]", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.people.crew[1]", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void garContainsPassengerContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
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
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN	
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.passengers").exists())
		.andExpect(jsonPath("$.people.passengers").isArray())
		.andExpect(jsonPath("$.people.passengers[0]", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.people.passengers[1]", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void garContainsFileUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.file_uuids").exists())
		.andExpect(jsonPath("$.file_uuids").isArray())
		.andExpect(jsonPath("$.file_uuids[0]", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.file_uuids[1]", matchesRegex(REGEX_UUID)));
	}	
	@Test
	public void garContainsAttributeContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_RETRIEVE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.attributes").exists())
		.andExpect(jsonPath("$.attributes").isBoolean());
	}
	//----------------------------------------
	/*
	 * Retrieving existing GAR in summary format.     SUMMARY SUMMARY SUMMARY
	 * @see EGAR-1010	
	 */
	@Test
	public void retrieveSummaryViewOfExistingGar() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}
	/**
	 * @see EGAR-1010	
	 * @throws Exception
	 */
	@Test
	public void noMatchRetrieveSummaryViewOfImaginaryGar() throws Exception{
		// WHEN
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isBadRequest());
	}

	/**
	 * @see EGAR-1010	
	 * @throws Exception
	 */
	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveSummaryViewOfExistingGar() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/summary",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}

	/*
	 * Checking GAR summary has an assigned GAR UUID
	 * @see EGAR-1010	
	 * @throws Exception
	 */
	@Test
	public void garSummaryContainsGarUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
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
	 * Checking GAR summary has an assigned user UUID
	 * @see EGAR-1010	
	 * @throws Exception
	 */
	@Test
	public void garSummaryContainsUserUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.user_uuid").exists())
		.andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));	
	}
	/*
	 * Checking GAR summary has an assigned aircraft details
	 * @see EGAR-1010	
	 * @throws Exception
	 */
	@Test
	public void garSummaryContainsAircraftDetailsContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.aircraftTestData()));
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.aircraft.registration").exists())
		.andExpect(jsonPath("$.aircraft.registration").isString())
		.andExpect(jsonPath("$.aircraft.type").exists())
		.andExpect(jsonPath("$.aircraft.type").isString())
		.andExpect(jsonPath("$.aircraft.base").exists())
		.andExpect(jsonPath("$.aircraft.base").isString())
		.andExpect(jsonPath("$.aircraft.taxesPaid").exists())
		.andExpect(jsonPath("$.aircraft.taxesPaid").isBoolean());
	}
	/*
	 * Checking GAR summary has an assigned location details 
	 */
	@Test
	public void garSummaryContainsLocationDetailsContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		// Create a Departure location
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(ICAO)))
		.andDo(print())
		.andExpect(status().isSeeOther())
		;
		// Create an Arrival Locations
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)))
		.andDo(print())
		.andExpect(status().isSeeOther())
		;
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.location").exists())
		.andExpect(jsonPath("$.location").isArray())
		.andExpect(jsonPath("$.location[0].leg_num").exists())
		.andExpect(jsonPath("$.location[0].leg_num").isNumber())
		.andExpect(jsonPath("$.location[0].leg_num").value(instanceOf(Integer.class)))
		.andExpect(jsonPath("$.location[0].leg_num").value(greaterThanOrEqualTo(0)))
		.andExpect(jsonPath("$.location[0].leg_count").exists())
		.andExpect(jsonPath("$.location[0].leg_count").isNumber())
		.andExpect(jsonPath("$.location[0].leg_count").value(instanceOf(Integer.class)))
		.andExpect(jsonPath("$.location[0].leg_count").value(greaterThanOrEqualTo(0)))
		.andExpect(jsonPath("$.location[0].datetime").exists())
		.andExpect(jsonPath("$.location[0].datetime", matchesRegex(REGEX_ZULU_DATETIME)))
		.andExpect(jsonPath("$.location[0].ICAO").exists())
		.andExpect(jsonPath("$.location[0].ICAO", matchesRegex(REGEX_ICAO_CODE)));
//		.andExpect(jsonPath("$.location[0].name").exists())
//		.andExpect(jsonPath("$.location[0].name").isString())
//		.andExpect(jsonPath("$.location[0].postcode").exists())
//		.andExpect(jsonPath("$.location[0].postcode", matchesRegex(REGEX_P0STCODE)))
//		.andExpect(jsonPath("$.location[0].point.latitude").exists())
//		.andExpect(jsonPath("$.location[0].point.latitude").isString())
//		.andExpect(jsonPath("$.location[0].point.longitude").exists())
//		.andExpect(jsonPath("$.location[0].point.longitude").isString());
	}
	/*
	 * Checking GAR summary has an assigned captain details 
	 */
	@Test
	public void garSummaryContainsCaptainDetailsContent() throws Exception{
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
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.captain.type").exists())
		.andExpect(jsonPath("$.people.captain.type",matchesRegex(REGEX_PERSON_TYPE)))
		.andExpect(jsonPath("$.people.captain.details.given_name").exists())
		.andExpect(jsonPath("$.people.captain.details.given_name").isString())
		.andExpect(jsonPath("$.people.captain.details.family_name").exists())
		.andExpect(jsonPath("$.people.captain.details.family_name").isString())
		.andExpect(jsonPath("$.people.captain.details.gender").exists())
		.andExpect(jsonPath("$.people.captain.details.gender",matchesRegex(REGEX_GENDER)))
		.andExpect(jsonPath("$.people.captain.details.address").exists())
		.andExpect(jsonPath("$.people.captain.details.address").isString())
		.andExpect(jsonPath("$.people.captain.details.dob").exists())
		.andExpect(jsonPath("$.people.captain.details.dob", matchesRegex(REGEX_DOB)))
		.andExpect(jsonPath("$.people.captain.details.place").exists())
		.andExpect(jsonPath("$.people.captain.details.place").isString())
		.andExpect(jsonPath("$.people.captain.details.nationality").exists())
		.andExpect(jsonPath("$.people.captain.details.nationality").isString())
		.andExpect(jsonPath("$.people.captain.details.document_type").exists())
		.andExpect(jsonPath("$.people.captain.details.document_type").isString())
		.andExpect(jsonPath("$.people.captain.details.document_no").exists())
		.andExpect(jsonPath("$.people.captain.details.document_no").isString())
		.andExpect(jsonPath("$.people.captain.details.document_expiryDate").exists())
		.andExpect(jsonPath("$.people.captain.details.document_expiryDate", matchesRegex(REGEX_DOB)))
		.andExpect(jsonPath("$.people.captain.details.document_issuingCountry").exists())
		.andExpect(jsonPath("$.people.captain.details.document_issuingCountry",matchesRegex(REGEX_COUNTRY_CODE)))
		;
	}	
	/*
	 * Checking GAR summary has an assigned crew and passengers details 
	 */
	@Test
	public void garSummaryContainsCrewDetailsContent() throws Exception{
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
				.content(TestDependacies.personTestData(CREW)));
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.crew").exists())
		.andExpect(jsonPath("$.people.crew").isArray());
	}	
	@Test
	public void garSummaryContainsPassengersDetailsContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
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
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.people.passengers").exists())
		.andExpect(jsonPath("$.people.passengers").isArray());
	}	
	/*
	 * Checking GAR summary contains files details
	 */
	@Test
	public void garSummaryContainsFilesDetailsContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.files").exists())
		.andExpect(jsonPath("$.files").isArray())
		.andExpect(jsonPath("$.files[0].file_uuid").exists())
		.andExpect(jsonPath("$.files[0].file_uuid", matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.files[0].file_name").exists())
		.andExpect(jsonPath("$.files[0].file_name").isString())
		.andExpect(jsonPath("$.files[0].file_status").exists())
		.andExpect(jsonPath("$.files[0].file_status").isString())
		.andExpect(jsonPath("$.files[0].file_link").exists())
		.andExpect(jsonPath("$.files[0].file_link").isString())
		.andExpect(jsonPath("$.files[0].file_size").exists())
		.andExpect(jsonPath("$.files[0].file_size").isNumber());
	}	
	/*
	 * Checking GAR summary contains attributes details
	 */
	@Test
	public void garSummaryContainsAttributesDetailsContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.attributeTestData()));
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.attributes.hazardous").exists())
		.andExpect(jsonPath("$.attributes.hazardous").isBoolean())
		.andExpect(jsonPath("$.attributes.responsible_person.contact_number").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.contact_number", matchesRegex(REGEX_CONTACT)))
		.andExpect(jsonPath("$.attributes.responsible_person.name").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.name").isString())
		.andExpect(jsonPath("$.attributes.responsible_person.type").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.type",matchesRegex(REGEX_PERSON_TYPE)))
		.andExpect(jsonPath("$.attributes.responsible_person.address").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.address").isString());
	}	

	/*
	 * Checking GAR summary contains submission details
	 */
	@Test
	public void garSummaryContainsSubmissionDetailsContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		// ADD Aircraft
		this.mockMvc
		.perform(post(AIRCRAFT_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)	
				.content(TestDependacies.aircraftTestData()))
		.andExpect(status().isSeeOther());
		// ADD Arrival 
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)))
		.andExpect(status().isSeeOther());
		// ADD Departure 
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(ICAO)))
		.andExpect(status().isSeeOther());
		// SUBMIT
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER, EMAIL)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andDo(print())
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull()) ))
		.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)));
		// GET summary
		this.mockMvc
		.perform(get(GAR_SUMMARY_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER, AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.submission").exists())
		.andExpect(jsonPath("$.submission.submission_uuid").exists())
		.andExpect(jsonPath("$.submission.submission_uuid",matchesRegex(REGEX_UUID)))
		.andExpect(jsonPath("$.submission.type").exists())
		.andExpect(jsonPath("$.submission.type",matchesRegex(REGEX_SUBMISSION_TYPE)))
		.andExpect(jsonPath("$.submission.external_ref").exists())
		.andExpect(jsonPath("$.submission.external_ref").isString())
		.andExpect(jsonPath("$.submission.status").exists())
		.andExpect(jsonPath("$.submission.status").isString())
		;

	}		
	//----------------------------------------
	/*
	 * POST
	 */
	//----------------------------------------
	/*
	 * Successfully creating a GAR and assigning it to new endpoint
	 */
	@Test
	public void createAGar() throws Exception{
		// WITH
		MvcResult result =
				// WHEN
				this.mockMvc
				.perform(post(GAR_SERVICE_NAME)
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				// THEN			
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull()) ))
				.andExpect(header().string("Location", startsWith(GAR_SERVICE_NAME) ))
				.andReturn();

		//		String location = result.getResponse().getHeader("Location");
		MockHttpServletResponse response = result.getResponse();
		String location = response.getRedirectedUrl();
		String uuid = location.substring(GAR_SERVICE_NAME.length(), location.length()-1);

		assertTrue(uuid.matches(REGEX_UUID));
	}
	//----------------------------------------

}
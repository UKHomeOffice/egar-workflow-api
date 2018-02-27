package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;
import uk.gov.digital.ho.egar.workflow.client.FileClient;
import uk.gov.digital.ho.egar.workflow.client.SubmissionClient;
import uk.gov.digital.ho.egar.workflow.client.dummy.DummyFileClientImpl;
import uk.gov.digital.ho.egar.workflow.client.dummy.DummySubmissionClient;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AIRCRAFT_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.ATTRIBUTE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.FILE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LOCATION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PERSON_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LocationType.ICAO;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.PersonType.CAPTAIN;

import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class SubmissionControllerTest {//


	@Rule
	public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

	@Autowired
	private WorkflowApplication app;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private SubmissionClient submissionClient;
	@Autowired 
	private FileClient fileClient; 
	@Autowired
	private WorkflowPropertiesConfig config;

	private TestDependacies retriever;

	@PostConstruct
	private void init() {
		retriever = new TestDependacies(mockMvc);
	}
	
	@Before
	public void setup(){
		final long time = -7200;
		setArrivalCancellationLimit(time);
		setFileStatus(FileStatus.AWAITING_VIRUS_SCAN);
	}

	@Test
	public void contextLoads() {
		assertThat(app).isNotNull();

	}

	//	SUBMISSION_SERVICE_NAME
	public void submitAGar(String garUuid) throws Exception {
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
		// SUBMIT
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)))
		;
	}

	//----------------------------------------
	/*
	 * GET
	 */
	//----------------------------------------
	/*
	 * Retrieve a submitted GAR
	 */
	@Test
	public void retrieveASubmittedGAR() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				.perform(get(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				.andDo(print())
				// THEN
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void noMatchRetrieveASubmittedGAR() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		final String alternateGarUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				.perform(get(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", alternateGarUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andDo(print())
				// THEN
				.andExpect(status().isBadRequest());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveASubmittedGAR() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		final String ALTERNATE_AUTH = "valuesSSSSS";
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				.perform(get(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, ALTERNATE_AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andDo(print())
				// THEN
				.andExpect(status().isUnauthorized());
	}

	/*
	 * Checking Submission has an assigned GAR UUID
	 */
	@Test
	public void submissionContainsGarUUIDContent() throws Exception {
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				.perform(get(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andDo(print())
				// THEN
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.gar_uuid").exists())
				.andExpect(jsonPath("$.gar_uuid", matchesRegex(REGEX_UUID)));
	}

	/*
	 * Checking Submission has an assigned USER UUID
	 */
	@Test
	public void submissionContainsUserUUIDContent() throws Exception {
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				.perform(get(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andDo(print())
				// THEN
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.user_uuid").exists())
				.andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));
	}

	/*
	 * Checking Submission has an assigned USER UUID
	 */
	@Test
	public void submissionContainsSubmissionContent() throws Exception {
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				.perform(get(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andDo(print())
				// THEN
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.submission").exists())
				.andExpect(jsonPath("$.submission.submission_uuid").exists())
				.andExpect(jsonPath("$.submission.submission_uuid", matchesRegex(REGEX_UUID)))
				.andExpect(jsonPath("$.submission.type").exists())
				.andExpect(jsonPath("$.submission.type").isString())
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
	@Test
	public void submitGar() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		setFileStatus(FileStatus.VIRUS_SCANNED);
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
		// ADD File
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		// SUBMIT
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)))
		;


	}

	@Test
	public void noMatchSubmitImaginaryGar() throws Exception {
		// WITH
		final String garUuid = UUID.randomUUID().toString();
		// WHEN
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				// THEN
				.andExpect(status().isForbidden());
	}

	@Ignore
	@Test
	public void unauthorizedSubmitGar() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		final String ALTERNATE_AUTH = "valuesSSSSS";
		// WHEN
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, ALTERNATE_AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				// THEN
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void forbiddenSubmitGar() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		submitAGar(garUuid);
		this.mockMvc
				// THEN
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andExpect(status().isForbidden());

	}

	@Test
	public void postCancelWhenGarDoesNotExist() throws Exception {
		// WITH
		final String garUuid = UUID.randomUUID().toString();
		// WHEN
		this.mockMvc
				.perform(delete(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				// THEN
				.andExpect(status().isForbidden());
	}

	@Test
	public void badRequestCancelWhenGarIsNotSubmitted() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
				.perform(delete(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				// THEN
				.andExpect(status().isBadRequest());

	}
@Ignore
	@Test
	public void forbiddenCancelSubmissionWithinArrivalAfterThreshold() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		setArrivalCancellationLimit(-18000);
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
		String arrivalData = TestDependacies.arrivalTestData(ZonedDateTime.now().plusHours(4));
		this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(arrivalData))
				.andExpect(status().isSeeOther());
		// ADD Departure
		this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
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
		// SUBMIT
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)))
		;
		// CANCEL SUBMIT
		this.mockMvc
				.perform(delete(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				// THEN
				.andExpect(status().isForbidden());
	}

	@Test
	public void successCancelSubmissionInBeforeThreshold() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
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
		String arrivalData = TestDependacies.arrivalTestData(ZonedDateTime.now().plusHours(4));
		this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(arrivalData))
				.andExpect(status().isSeeOther());
		// ADD Departure
		this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
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
		// SUBMIT
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)));
	}
	
	
	@Test
	public void forbiddenCancelSubmissionWhenAlreadyCancelled() throws Exception {
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
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
		String arrivalData = TestDependacies.arrivalTestData(ZonedDateTime.now().plusHours(4));
		this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(arrivalData))
				.andExpect(status().isSeeOther());
		// ADD Departure
		this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
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
		// SUBMIT
		this.mockMvc
				.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL))
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)))
		;
		// CANCEL SUBMIT
		this.mockMvc
				.perform(delete(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid)));

		// CANCEL SUBMIT SECOND TIME
		this.mockMvc
				.perform(delete(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.header(EMAIL_HEADER, EMAIL)
						.contentType(APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isForbidden());
	}
	
	@Test 
	  public void forbiddenToSubmitIfFileNotVirusScanned() throws Exception{ 
		// WITH
		 String garUuid = retriever.createAGar(USER_UUID, AUTH);
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
			// ADD File
			this.mockMvc
			.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
					.header(USERID_HEADER, USER_UUID)
					.header(AUTH_HEADER,  AUTH)
					.contentType(APPLICATION_JSON_UTF8_VALUE)
					.content(TestDependacies.fileTestData()))
			.andExpect(status().isSeeOther());
			// SUBMIT
			this.mockMvc
					.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
							.header(USERID_HEADER, USER_UUID)
							.header(AUTH_HEADER, AUTH)
							.header(EMAIL_HEADER, EMAIL))
					// THEN
					.andExpect(status().isForbidden());
	  } 
	
	private void setArrivalCancellationLimit(long time){
		if (submissionClient instanceof DummySubmissionClient){
			config.setArrivalCancellaionLimit(time);
		}
	}
	
		
	private void setFileStatus(FileStatus fileStatus) {
		if (fileClient instanceof DummyFileClientImpl) {
			((DummyFileClientImpl)fileClient).setFileStatus(fileStatus);
		}
	}
}
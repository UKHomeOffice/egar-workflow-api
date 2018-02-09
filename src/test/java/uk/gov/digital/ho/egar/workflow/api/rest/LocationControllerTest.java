package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.*;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.co.civica.microservice.util.testing.utils.FileReaderUtils.readFileAsString;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LOCATION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LocationType.ICAO;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LocationType.IATA;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.LocationType.POINT;

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
import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;


public abstract class LocationControllerTest {

	@Rule
	public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

	@Autowired
	private WorkflowApplication app;
	@Autowired
	protected MockMvc mockMvc;

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

	private void addDepartureLocationToGar(String garUuid) throws Exception{
		final String startOfLocationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
															   .replace("{option}/", "");
		MvcResult result =
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "dept"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.departureTestData(ICAO)))
				.andDo(print())
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(startOfLocationUri)))
				.andReturn();
		String locationUri = result.getResponse().getHeader("Location");
		String uuid = locationUri.substring(startOfLocationUri.length());
		assertTrue(uuid.matches(REGEX_UUID));
		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, locationUri);
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("ICAO").asText(), 	("EBBT"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), ("2018-10-21T10:15:30Z"));

	}	
	//----------------------------------------
	/*
	 * GET
	 */
	//----------------------------------------
	/*
	 * Retrieving all eGAR flight locations from end point
	 */
	@Test
	public void retrieveAllEgarFlightLocations() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}/", ""))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));	
	}
	//----------------------------------------
	/*
	 * Retrieving all eGAR flight locations from end point
	 */
	@Test
	public void canNotAddInvalidFlightLocations() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		final String locationJson = readFileAsString("data/LocationTestData/InvalidLocationTestData.json");

		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   .replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(locationJson))
		.andDo(print())
		.andExpect(status().is4xxClientError())
		.andExpect(status().isForbidden());

		// THEN
	}
	@Test
	public void noMatchRetrieveAllEgarFlightLocationsForImaginaryGAR() throws Exception{
		// WITH
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString())
										  .replace("{option}/", ""))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isForbidden());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveAllEgarFlightLocations() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/locations/",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	/*
	 * Checking eGAR flight location has an assigned GAR UUID
	 */
	@Test
	public void flightLocationContainGarUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}/", ""))
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
	 * Checking eGAR flight location has an assigned user UUID
	 */
	@Test
	public void flightLocationContainUserUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}/", ""))
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
	 * Checking eGAR flight location has an assigned location UUID
	 */
	@Test
	public void flightLocationContainsLocationUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}/", ""))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.location_uuids").exists())
		.andExpect(jsonPath("$.location_uuids").isArray())
		.andExpect(jsonPath("$.location_uuids[0]", matchesRegex(REGEX_UUID)));
	}
	//----------------------------------------
	/*
	 * Retrieving single eGAR flight location from endpoint
	 */
	@Test
	public void retrieveEgarFlightLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		String LocationUUID =retriever.retrieveLocationUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}", LocationUUID))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));	
	}
	@Test
	public void noMatchRetrieveEgarFlightLocation() throws Exception{
		// WITH
		final String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isBadRequest());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveEgarFlightLocation() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/locations/%s/",retriever.createAGar(USER_UUID, AUTH),retriever.retrieveLocationUUID(USER_UUID, AUTH, retriever.createAGar(USER_UUID, AUTH))))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	/*
	 * Checking eGAR flight location has an assigned GAR UUID
	 */
	@Test
	public void flightLocationContainsGarUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		String locationUri =LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{option}/", "");
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												   .replace("{option}", "dept"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.departureTestData(ICAO)))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(locationUri)))
				.andReturn();
		// WITH
		String header = result.getResponse().getHeader("Location");
		String LocationUUID = header.substring(locationUri.length());
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}",LocationUUID ))
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
	 * Checking eGAR flight location has an assigned user UUID
	 */
	@Test
	public void flightLocationContainsUserUUIDContent() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		String LocationUUID =retriever.retrieveLocationUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}",LocationUUID ))
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
	 * Checking eGAR flight location has assigned location details
	 */
	@Test
	public void flightLocationContainsLegNum() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		String LocationUUID =retriever.retrieveLocationUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}",LocationUUID ))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.location.leg_num").exists())
		.andExpect(jsonPath("$.location.leg_num").isNumber())
		.andExpect(jsonPath("$.location.leg_num").value(instanceOf(Integer.class)))
		.andExpect(jsonPath("$.location.leg_num").value(greaterThanOrEqualTo(0)));
	}
	@Test
	public void flightLocationContainsLegCount() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		String LocationUUID =retriever.retrieveLocationUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}",LocationUUID ))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.location.leg_count").exists())
		.andExpect(jsonPath("$.location.leg_count").isNumber())
		.andExpect(jsonPath("$.location.leg_count").value(instanceOf(Integer.class)))
		.andExpect(jsonPath("$.location.leg_count").value(greaterThanOrEqualTo(0)));
	}
	@Test
	public void flightLocationContainsDateTime() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		String LocationUUID =retriever.retrieveLocationUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}",LocationUUID ))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.location.datetime").exists())
		.andExpect(jsonPath("$.location.datetime", matchesRegex(REGEX_ZULU_DATETIME)));
	}
	//----------------------------------------
	/*
	 * Retrieving departure details for existing GAR from end point
	 */
	@Test
	public void retrieveDepartureDetailsOfExistingGar() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		addDepartureLocationToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}","dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}
	@Test
	public void noMatchRetrieveDepartureDetailsOfExistingGar() throws Exception{
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString())
										  .replace("{option}","dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN	
		.andExpect(status().isForbidden());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveDepartureDetailsOfExistingGar() throws Exception{

		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/locations/dept/",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	/*
	 * Retrieving arrival details for existing GAR from end point
	 */
	@Test
	public void retrieveArrivalDetailsOfExistingGar() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);	
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   .replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)));

		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										  .replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}
	@Test
	public void noMatchRetrieveArrivalDetailsOfExistingGar() throws Exception{
		// WHEN
		this.mockMvc
		.perform(get(LOCATION_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString())
										  .replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isForbidden());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveArrivalDetailsOfExistingGar() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/locations/arr/",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	//----------------------------------------
	/*
	 * icao check to see if skips regex validation for empty string.
	 * This will only work when the Location REST is checking the location. 
	 */
	@Test
	@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
	public void icoaRegexValidationSkipForEmptyString() throws Exception{
		// WITH
		final String emptyIcao = readFileAsString("data/LocationTestData/InvalidTestDataEmptyICAO.json");
		final String garUuid =retriever.createAGar(USER_UUID, AUTH);	
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   .replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(emptyIcao))
		.andDo(print())
		.andExpect(status().isForbidden());
	}

	/*
	 * dateTime check to see if triggers json dezerializer
	 */
	@Test
	public void dateTimeRegexValidationSkipForEmptyString() throws Exception{
		// WITH
		final String emptyDateTime = readFileAsString("data/LocationTestData/InvalidTestDataEmptyDateTime.json");
		final String garUuid =retriever.createAGar(USER_UUID, AUTH);	
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
				   						   .replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(emptyDateTime))
		.andDo(print())
		.andExpect(status().is3xxRedirection());
	}

	//----------------------------------------
	/*
	 * POST
	 */
	//----------------------------------------
	/*

	 */
	/*
	 * Adding a dept location
	 * @see EGAR-900
	 */
	@Test
	public void addingDeptIcaoLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		final String startOfLocationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
															   .replace("{option}/", "");
		MvcResult result =
				// WHEN
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												   .replace("{option}", "dept"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.departureTestData(ICAO)))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(startOfLocationUri)))
				.andReturn();

		String locationUri = result.getResponse().getHeader("Location");
		String uuid = locationUri.substring(startOfLocationUri.length());

		assertTrue(uuid.matches(REGEX_UUID));

		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, locationUri);
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("ICAO").asText(), 					("EBBT"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), 				("2018-10-21T10:15:30Z"));
	}
	@Test
	public void addingDeptIataLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		final String startOfLocationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
															   .replace("{option}/", "");
		MvcResult result =
				// WHEN
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												   .replace("{option}", "dept"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.departureTestData(IATA)))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(startOfLocationUri)))
				.andReturn();

		String locationUri = result.getResponse().getHeader("Location");
		String uuid = locationUri.substring(startOfLocationUri.length());

		assertTrue(uuid.matches(REGEX_UUID));

		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, locationUri);
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("IATA").asText(), 					("BRS"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), 				("2018-10-21T10:15:30Z"));
	}
	
	@Test
	public void addingDeptPointLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		final String startOfLocationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
															   .replace("{option}/", "");
		MvcResult result =
				// WHEN
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												   .replace("{option}", "dept"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.departureTestData(POINT)))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(startOfLocationUri)))
				.andReturn();

		String locationUri = result.getResponse().getHeader("Location");
		String uuid = locationUri.substring(startOfLocationUri.length());

		assertTrue(uuid.matches(REGEX_UUID));

		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, locationUri);
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("datetime").asText(), 				("2017-10-21T10:15:30Z"));
		assertEquals(locationResponse.get("location").get("point").get("latitude").asText(), 	("1.50"));
		assertEquals(locationResponse.get("location").get("point").get("longitude").asText(),	("1.50"));
	}

	@Test
	public void noMatchAddingDeptLocation() throws Exception{
		// WITH
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString())
										   .replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(ICAO)))
		// THEN
		.andExpect(status().isForbidden());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedAddingDeptLocation() throws Exception{
		this.mockMvc
		.perform(post(String.format("/api/v1/WF/GARs/%s/locations/dept",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isUnauthorized());
	}
	@Test
	public void forbiddenAddingDeptLocation() throws Exception{
		// WITH 
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL));
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   .replace("{option}", "dept"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(ICAO)))
		// THEN
		.andExpect(status().isForbidden());
	}
	/*
	 * Amending a dept location
	 * @see EGAR-900
	 */

	@Test
	public void amendingDeptLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		String locationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												  .replace("{option}/", "");
		String deptLocationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
													  .replace("{option}", "dept");
		// WHEN
		
		this.mockMvc
		.perform(post(deptLocationUri)
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(ICAO)))
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location",startsWith(locationUri)))
		;
		// THEN
		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, deptLocationUri);
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("ICAO").asText(), 					("EBBT"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), 				("2018-10-21T10:15:30Z"));
		// WHEN
		this.mockMvc
		.perform(post(deptLocationUri)
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.departureTestData(IATA)))
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location",startsWith(locationUri)))
		;
		// THEN
		JsonNode amendedLocationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, deptLocationUri);
		assertThat(amendedLocationResponse).isNotNull();
		assertThat(amendedLocationResponse.has("location")).isTrue();
		assertEquals(amendedLocationResponse.get("location").get("ICAO").asText(), 		("null"));
		assertEquals(amendedLocationResponse.get("location").get("datetime").asText(), 	("2018-10-21T10:15:30Z"));
		assertEquals(amendedLocationResponse.get("location").get("IATA").asText(), 	("BRS"));
	}
	/*
	 * Adding a arr location
	 * @see EGAR-672
	 */
	@Test
	public void addingArrIcaoLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);	
		String locationUri =LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   		 .replace("{option}/", "");
		String arrLocationUri = LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   			 .replace("{option}", "arr");
		// WHEN
		
		MvcResult result =
				this.mockMvc
				.perform(post(arrLocationUri)
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.arrivalTestData(ICAO)))
				// THEN			
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(locationUri)))
				.andReturn();
		// WITH
		String header = result.getResponse().getHeader("Location");
		String uuid = header.substring(locationUri.length());
		// THEN
		assertTrue(uuid.matches(REGEX_UUID));
		// THEN
		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, arrLocationUri);
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("ICAO").asText(), 	("EBBA"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), ("2018-10-21T12:15:30Z"));
	}	
	@Test
	public void addingArrIataLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);	
		String locationUri =LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{option}/", "");
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												   .replace("{option}", "arr"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.arrivalTestData(IATA)))
				// THEN			
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(locationUri)))
				.andReturn();
		// WITH
		String header = result.getResponse().getHeader("Location");
		String uuid = header.substring(locationUri.length());
		// THEN
		assertTrue(uuid.matches(REGEX_UUID));
		// THEN
		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"));
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("IATA").asText(), 					("BHX"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), 				("2018-10-21T12:15:30Z"));
	}
	
	@Test
	public void addingArrPointLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);	
		String locationUri =LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}/", "");
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,   AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.arrivalTestData(POINT)))
				// THEN			
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location",startsWith(locationUri)))
				.andReturn();
		// WITH
		String header = result.getResponse().getHeader("Location");
		String uuid = header.substring(locationUri.length());
		// THEN
		assertTrue(uuid.matches(REGEX_UUID));
		// THEN
		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"));
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("point").get("latitude").asText(), 	("1.00"));
		assertEquals(locationResponse.get("location").get("point").get("longitude").asText(),	("1.00"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), 				("2017-10-21T12:15:30Z"));
	}
	@Test
	public void noMatchAddingArrLocation() throws Exception{
		// WITH
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()).replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)))
		// THEN
		.andExpect(status().isForbidden());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedAddingArrLocation() throws Exception{
		this.mockMvc
		.perform(post(String.format("/api/v1/WF/GARs/%s/locations/arr",retriever.createAGar(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isUnauthorized());
	}
	@Test
	public void forbiddenAddingArrLocation() throws Exception{
		// WITH 
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL));
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid).replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)))
		// THEN
		.andExpect(status().isForbidden());
	}
	@Test
	public void amendingArrLocation() throws Exception{
		// WITH
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		String locationUri =LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{option}/", "");
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   .replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(ICAO)))
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location",startsWith(locationUri)))
		;
		// THEN
		JsonNode locationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
																										 .replace("{option}", "arr"));
		assertThat(locationResponse).isNotNull();
		assertThat(locationResponse.has("location")).isTrue();
		assertEquals(locationResponse.get("location").get("ICAO").asText(), 	("EBBA"));
		assertEquals(locationResponse.get("location").get("datetime").asText(), ("2018-10-21T12:15:30Z"));
		// WHEN
		this.mockMvc
		.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
										   .replace("{option}", "arr"))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.arrivalTestData(IATA)))
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))
		.andExpect(header().string("Location",startsWith(locationUri)))
		;
		// THEN
		JsonNode amendedLocationResponse = retriever.getContentAsJsonNode(USER_UUID, AUTH, LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
																												.replace("{option}", "arr"));
		assertThat(amendedLocationResponse).isNotNull();
		assertThat(amendedLocationResponse.has("location")).isTrue();
		assertEquals(amendedLocationResponse.get("location").get("ICAO").asText(), 		("null"));
		assertEquals(amendedLocationResponse.get("location").get("datetime").asText(), 	("2018-10-21T12:15:30Z"));
		assertEquals(amendedLocationResponse.get("location").get("IATA").asText(), 		("BHX"));
	}

}

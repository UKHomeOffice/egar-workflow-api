package uk.gov.digital.ho.egar.workflow.api.rest;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_CONTACT;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_RESPONSIBLE_PERSON_TYPE;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.co.civica.microservice.util.testing.utils.FileReaderUtils.readFileAsString;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.ATTRIBUTE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.test.web.servlet.MvcResult;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;

public abstract class AttributeControllerTest {

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

	private void addAttributesToGar(String garUuid) throws Exception {

		this.mockMvc
		.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.attributeTestData()))
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))				
		.andExpect(header().string("Location",ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid)));
		JsonNode attributeResponse = retriever.getContentAsJsonNode(USER_UUID,AUTH, ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid));
		assertThat(attributeResponse).isNotNull();
		assertThat(attributeResponse.has("attributes")).isTrue();
		assertEquals(attributeResponse.get("attributes").get("hazardous").asText(), ("false"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("address").asText(), 	   ("123 ABC"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("contact_number").asText(), ("01234567890"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("name").asText(), 		   ("Samantha"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("type").asText(), 		   ("CAPTAIN"));
	
	}
	//----------------------------------------
	/*
	 * GET
	 */
	//----------------------------------------
	/*
	 * 	Retrieve attribute details for an existing GAR
	 */

	@Test
	public void retrieveAttributeDetailsOfExistingGar() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAttributesToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));	
	}

	@Test
	public void noMatchRetrieveAttributeDetailsOfExistingGar() throws Exception{
		// WHEN
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN		
		.andExpect(status().isBadRequest());	
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveAttributeDetailsOfExistingGar() throws Exception{
		this.mockMvc
		.perform(get(String.format("/api/v1/WF/GARs/%s/attributes/",retriever.retrieveGarUUID(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());	
	}
	/*
	 * Checking GAR that is assigned attributes is assigned a GAR UUID
	 */
	@Test
	public void attributesContainGarUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAttributesToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isOk())
		// THEN	
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.gar_uuid").exists())
		.andExpect(jsonPath("$.gar_uuid", matchesRegex(REGEX_UUID)));
	}
	/*
	 * Checking GAR that is assigned attributes is assigned a user UUID
	 */
	@Test
	public void attributesContainUserUUIDContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAttributesToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isOk())
		// THEN
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.user_uuid").exists())
		.andExpect(jsonPath("$.user_uuid", matchesRegex(REGEX_UUID)));
	}
	/*
	 * Checking GAR is assigned hazardous attribute of boolean value
	 */
	@Test
	public void attributesContainHazardousContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAttributesToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isOk())
		// THEN		
		.andExpect(jsonPath("$.attributes.hazardous").exists())
		.andExpect(jsonPath("$.attributes.hazardous").isBoolean());
	}
	/*
	 * Checking  hazardous attribute is set to null if unassigned
	 */
	@Test
	public void attributesContainNullHazardousContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		String attribute = readFileAsString("data/AttributeTestData/NoBooleansSetAttributeTestData.json");
		// WHEN
		this.mockMvc
		.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(attribute));
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.attributes.hazardous").isEmpty());
	}


	/*
	 * Checking GAR is assigned responsible person details
	 */
	@Test
	public void attributesContainResponsiblePersonContent() throws Exception{
		// WITH
		String garUuid= retriever.createAGar(USER_UUID, AUTH);
		addAttributesToGar(garUuid);
		// WHEN
		this.mockMvc
		.perform(get(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isOk())
		// THEN		
		.andExpect(jsonPath("$.attributes.responsible_person.contact_number").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.contact_number", matchesRegex(REGEX_CONTACT)))
		.andExpect(jsonPath("$.attributes.responsible_person.name").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.name").isString())
		.andExpect(jsonPath("$.attributes.responsible_person.type").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.type", matchesRegex(REGEX_RESPONSIBLE_PERSON_TYPE)))
		.andExpect(jsonPath("$.attributes.responsible_person.address").exists())
		.andExpect(jsonPath("$.attributes.responsible_person.address").isString());
		;
	}
	//----------------------------------------
	/*
	 * POST
	 */
	//----------------------------------------
	/*
	 * Adding attribute data for existing GAR
	 */
	@Test
	public void addingAttributeToExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.attributeTestData()))
		// THEN
		.andExpect(status().isSeeOther())
		.andExpect(header().string("Location", not(isNull())))				
		.andExpect(header().string("Location",ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid)));
		JsonNode attributeResponse = retriever.getContentAsJsonNode(USER_UUID,AUTH,ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid));
		assertThat(attributeResponse).isNotNull();
		assertThat(attributeResponse.has("attributes")).isTrue();
		assertEquals(attributeResponse.get("attributes").get("hazardous").asText(), ("false"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("address").asText(), 	   ("123 ABC"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("contact_number").asText(), ("01234567890"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("name").asText(), 		   ("Samantha"));
		assertEquals(attributeResponse.get("attributes").get("responsible_person").get("type").asText(), 		   ("CAPTAIN"));
	}
	@Test
	public void noMatchAddingAttributeToImaginaryGAR() throws Exception{
		// WITH
		String garUuid = UUID.randomUUID().toString();
		// WHEN
		this.mockMvc
		.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.attributeTestData()))
		// THEN		
		.andExpect(status().isBadRequest());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedAddingAttributeToExistingGAR() throws Exception{
		this.mockMvc
		.perform(post(String.format("/api/v1/WF/GARs/%s/attributes/",retriever.retrieveGarUUID(USER_UUID, AUTH)))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isUnauthorized());
	}		

	@Test
	public void forbiddenAddingAttributeToExistingGAR() throws Exception{
		// WITH 
		String garUuid =retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL));
		this.mockMvc
		.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.attributeTestData()))
		.andExpect(status().isForbidden());
	}

	@Test
	public void maxLengthForAttributesExceeded() throws Exception {

		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		MvcResult result = this.mockMvc
				.perform(post(ATTRIBUTE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER, AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.invalidAttributeTestData()))
				// THEN
				.andExpect(status().isBadRequest())
				.andReturn();

		//Verify
		String resp = result.getResponse().getContentAsString();
		with(resp).assertThat("$.message[*]", hasItems("otherDetails.name: size must be between 0 and 35","otherDetails.contactNumber: size must be between 0 and 35"));
	}

}

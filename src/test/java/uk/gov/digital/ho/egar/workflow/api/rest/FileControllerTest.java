package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.AUTH_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.EMAIL_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.FILE_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.SUBMISSION_SERVICE_NAME;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USERID_HEADER;
import static uk.gov.digital.ho.egar.workflow.api.rest.TestDependacies.USER_UUID;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.co.civica.microservice.util.testing.utils.IgnoreWhenRestClients;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.gov.digital.ho.egar.workflow.WorkflowApplication;
import uk.gov.digital.ho.egar.workflow.client.FileClient;
import uk.gov.digital.ho.egar.workflow.client.FileInfoClient;
import uk.gov.digital.ho.egar.workflow.client.dummy.DummyFileClientImpl;
import uk.gov.digital.ho.egar.workflow.client.dummy.DummyFileInfoClientImpl;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.FileStatus;

public abstract class FileControllerTest {
	
	@Rule
	public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

	@Autowired
	private WorkflowApplication app;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
    private WorkflowPropertiesConfig config;

	@Autowired
	private FileInfoClient fileInfoClient;
	
	@Autowired
	private FileClient fileClient;


	private TestDependacies retriever ;
	
	@PostConstruct
	private void init()
	{
		retriever = new TestDependacies(mockMvc); 
	}
	
	@Before
	public void setup(){
		final long fileSize = 131072;
		setFileSize(fileSize);
		setFileStatus(FileStatus.AWAITING_VIRUS_SCAN);
	}

	@Test
	public void contextLoads() {
		assertThat(app).isNotNull();
	}
	
	private static final String INDIVIDUAL_FILE_SERVICE_NAME = FILE_SERVICE_NAME + "{file_uuid}/";
	
	private void addAFile(String garUuid) throws Exception{
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,  AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.fileTestData()))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", startsWith(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
				.andReturn();
		String fileUri =result.getResponse().getHeader("Location");
		String fileUuid = fileUri.substring(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(),fileUri.length()-1);

		assertTrue(fileUuid.matches(REGEX_UUID));

		JsonNode fileResponse = retriever.getContentAsJsonNode(USER_UUID, 
															   AUTH, 
															   INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
															   							   .replace("{file_uuid}", fileUuid));
		assertThat(fileResponse).isNotNull();
		assertThat(fileResponse.has("file")).isTrue();
		assertEquals(fileResponse.get("file").get("file_name").asText(), 	"test.txt");
		assertEquals(fileResponse.get("file").get("file_size").asText(),	"131072");
		assertEquals(fileResponse.get("file").get("file_status").asText(), 	"AWAITING_VIRUS_SCAN");
		assertEquals(fileResponse.get("file").get("file_link").asText(), 	"https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt");
	}
	
	//----------------------------------------
	/*
	 * GET
	 */
	//----------------------------------------
	/*
	 * Retrieving a list of files for an existing GAR
	 */
	@Test
	public void retrieveListOfFilesForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		addAFile(garUuid);
		this.mockMvc
		.perform(get(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}
	@Test
	public void noMatchRetrieveListOfFilesForImaginaryGAR() throws Exception{
		// WITH
		// WHEN
		this.mockMvc
		.perform(get(FILE_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isForbidden());
	}
	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveListOfFilesForExistingGAR() throws Exception{
		this.mockMvc
		.perform(get(FILE_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	/*
	 * Checking files are assigned a GAR UUID
	 */
	@Test
	public void listOfFilesContainGarUUIDContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		addAFile(garUuid);
		this.mockMvc
		.perform(get(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
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
	 * Checking files are assigned a user UUID
	 */
	@Test
	public void listOfFilesContainUserUUIDContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		addAFile(garUuid);
		this.mockMvc
		.perform(get(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
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
	 * Checking files are assigned file UUIDS
	 */
	@Test
	public void listOfFilesContainFilesUUIDContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		addAFile(garUuid);
		addAFile(garUuid);
		this.mockMvc
		.perform(get(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
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
	//----------------------------------------
	/*
	 * Retrieve a file for an existing GAR
	 */
	@Test
	public void retrieveFileForExisting() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
	}
	@Test
	public void noMatchRetrievingImaginaryFileForExistingGar() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isBadRequest());
	}
	
	
	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedRetrieveFileForExisting() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isUnauthorized());
	}
	/*
	 * Checking file is assigned a GAR UUID
	 */
	@Test
	public void fileContainsGarUUIDContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
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
	 * Checking file is assigned a user UUID
	 */
	@Test
	public void fileContainsUserUUIDContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
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
	 * Checking file is assigned file UUID, file name, file status, file link and file size
	 */
	@Test
	public void fileContainsFileUUIDContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.file.file_uuid").exists())
		.andExpect(jsonPath("$.file.file_uuid", matchesRegex(REGEX_UUID)));
	}
	@Test
	public void fileContainsFileNameContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.file.file_name").exists())
		.andExpect(jsonPath("$.file.file_name").isString());
	}
	@Test
	public void fileContainsFileStatusContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.file.file_status").exists())
		.andExpect(jsonPath("$.file.file_status").isString());
	}
	@Test
	public void fileContainsFileLinkContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.file.file_link").exists())
		.andExpect(jsonPath("$.file.file_link").isString());
	}
	@Test
	public void fileContainsFileSizeContent() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andDo(print())
		// THEN
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.file.file_size").exists())
		.andExpect(jsonPath("$.file.file_size").isNumber());
	}
	 
	//----------------------------------------
	/*
	 * POST
	 */
	//----------------------------------------
	@Test
	public void uploadFile() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, USER_UUID)
						.header(AUTH_HEADER,  AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(TestDependacies.fileTestData()))
				// THEN
				.andExpect(status().isSeeOther())
				.andExpect(header().string("Location", not(isNull())))
				.andExpect(header().string("Location", startsWith(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
				.andReturn();
		String fileUri =result.getResponse().getHeader("Location");
		String fileUuid = fileUri.substring(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid).length(),fileUri.length()-1);

		assertTrue(fileUuid.matches(REGEX_UUID));
		
		JsonNode fileResponse = retriever.getContentAsJsonNode(USER_UUID, 
															   AUTH, 
															   INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
															   							   .replace("{file_uuid}", fileUuid));
		assertThat(fileResponse).isNotNull();
		assertThat(fileResponse.has("file")).isTrue();
		assertEquals(fileResponse.get("file").get("file_name").asText(), 	"test.txt");
		assertEquals(fileResponse.get("file").get("file_size").asText(),	"131072");
		assertEquals(fileResponse.get("file").get("file_status").asText(), 	"AWAITING_VIRUS_SCAN");
		assertEquals(fileResponse.get("file").get("file_link").asText(), 	"https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt");
	}
	
	@Test
	public void noMatchUploadingFileToImaginaryGar() throws Exception{
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		// THEN
		.andExpect(status().isForbidden());
	}
	//TODO When Authentication is added
	@Ignore
	@Test
	public void unauthorisedUploadOfFile() throws Exception{
		
	}
	
	@Test
	public void forbiddenUploadOfFileToSubmittedGar() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(post(SUBMISSION_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH)
				.header(EMAIL_HEADER,  EMAIL));
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		// THEN
		.andExpect(status().isForbidden());
	}
	
	@Test
	public void forbiddenToUploadOverMaxNumberOfFiles() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		for(int i =0; i < config.getMaxFileNumber(); i++){
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		}
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@ConditionalIgnore( condition = IgnoreWhenRestClients.class )
	public void forbiddenToGoOverMaxTotalFileSizeMultipleFiles() throws Exception{
		// WITH
		final long fileSize = 262144;
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		setFileSize(fileSize);
		for(int i =0; i < 2; i++){
			
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isSeeOther());
		}
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData()))
		.andExpect(status().isForbidden());
	}
	@Test
	public void forbiddenToGoOverMaxTotalFileSizeSingleFile() throws Exception{
		// WITH
		final long fileSize = 786432;
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		setFileSize(fileSize);
		this.mockMvc
		.perform(post(FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,  AUTH)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.content(TestDependacies.fileTestData("Biggest")))
		.andExpect(status().isForbidden());
	}
	
	
	//----------------------------------------
	/*
	 * DELETE
	 */
	//----------------------------------------
	/*
	 * Delete a file for an existing GAR
	 */
	@Test
	public void deleteFileForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(delete(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 	.replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andExpect(status().isAccepted());

	}
	@Test
	public void noMatchDeleteImaginaryFileForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		// WHEN
		this.mockMvc
		.perform(get(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 .replace("{file_uuid}",UUID.randomUUID().toString()))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andExpect(status().isBadRequest());
	}

	@Ignore // TODO When Authentication added
	@Test
	public void unauthorisedDeleteFileForExistingGAR() throws Exception{
		// WITH
		String garUuid = retriever.createAGar(USER_UUID, AUTH);
		addAFile(garUuid);
		String fileUuid = retriever.retrieveFileUUID(USER_UUID, AUTH, garUuid);
		// WHEN
		this.mockMvc
		.perform(delete(INDIVIDUAL_FILE_SERVICE_NAME.replace("{gar_uuid}", garUuid)
												 	.replace("{file_uuid}",fileUuid))
				.header(USERID_HEADER, USER_UUID)
				.header(AUTH_HEADER,   AUTH))
		.andExpect(status().isUnauthorized());
	}
	
	private void setFileSize(long fileSize){
		if (fileInfoClient instanceof DummyFileInfoClientImpl){
			((DummyFileInfoClientImpl)fileInfoClient).setFileSize(fileSize);
		}
	}

	private void setFileStatus(FileStatus fileStatus){
		if (fileClient instanceof DummyFileClientImpl){
			((DummyFileClientImpl)fileClient).setFileStatus(fileStatus);
		}
	}
}

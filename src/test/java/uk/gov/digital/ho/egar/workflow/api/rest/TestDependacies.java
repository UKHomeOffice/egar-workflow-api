package uk.gov.digital.ho.egar.workflow.api.rest;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.civica.microservice.util.testing.matcher.RegExConstants.REGEX_UUID;
import static uk.co.civica.microservice.util.testing.utils.FileReaderUtils.readFileAsString;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class TestDependacies {
	
	public static final String USERID_HEADER              = "x-auth-subject";
	public static final String AUTH_HEADER 				  = "Authorization";
	public static final String EMAIL_HEADER               = "x-auth-email";
	public static final String FORENAME_HEADER            = "x-auth-given-name";
	public static final String SURNAME_HEADER             = "x-auth-family-name";
	public static final String CONTACT_HEADER             = "x-auth-contact"; //FIXME need header.this is wrong
	public static final String ALTERNATIVE_CONTACT_HEADER = "x-auth-alt-contact"; //FIXME need header.this is wrong
	
	public static final UUID   USER_UUID 				  =  UUID.randomUUID();
	public static final String AUTH 					  = "values";
	public static final String EMAIL                      = "123@knb.com";
//	private static final String FORENAME = "hamza";
//	private static final String SURNAME = "khaleel";
//	private static final String CONTACT = "+441234567890";
//	private static final String ALT_CONTACT = "+440123456789";
	

	public static final String GAR_SERVICE_NAME        				= "/api/v1/WF/GARs/";
	public static final String GAR_RETRIEVE_SERVICE_NAME       		= "/api/v1/WF/GARs/{gar_uuid}/";
	public static final String GAR_SUMMARY_SERVICE_NAME       		= "/api/v1/WF/GARs/{gar_uuid}/summary";
	public static final String SUBMISSION_SERVICE_NAME 				= "/api/v1/WF/GARs/{gar_uuid}/Submission/";
	public static final String AIRCRAFT_SERVICE_NAME   				= "/api/v1/WF/GARs/{gar_uuid}/aircraft/";
	public static final String LOCATION_SERVICE_NAME   				= "/api/v1/WF/GARs/{gar_uuid}/locations/{option}/";
	public static final String PERSON_SERVICE_NAME     				= "/api/v1/WF/GARs/{gar_uuid}/persons/";
	public static final String ATTRIBUTE_SERVICE_NAME  				= "/api/v1/WF/GARs/{gar_uuid}/attributes/";
	public static final String FILE_SERVICE_NAME					= "/api/v1/WF/GARs/{gar_uuid}/files/";
	
	private final MockMvc mockMvc ;
	
	public TestDependacies(MockMvc mockMvc) {
		this.mockMvc = mockMvc ;
	}
	/*
	 * Create a new gar
	 */
	public String createAGar(UUID userUuid, String auth) throws Exception{
		MvcResult result =
			this.mockMvc
					.perform(post(GAR_SERVICE_NAME)
							.header(USERID_HEADER, userUuid)
							.header(AUTH_HEADER, auth))
					.andDo(print())
					.andExpect(status().isSeeOther())
					.andExpect(header().string("Location", not(isNull()) ))
					.andExpect(header().string("Location", startsWith(GAR_SERVICE_NAME)))
					.andReturn();
		
		String garLocation = result.getResponse().getHeader("Location");
		String uuid = garLocation.substring(GAR_SERVICE_NAME.length(), garLocation.length()-1);
		
		assertTrue(uuid.matches(REGEX_UUID));
		
		return uuid ;
	}
	
	/*
	 * create new location for existing Gar
	 */
	
	public String createALocation(UUID userUuid, String auth,String garUuid) throws Exception{
		MvcResult result =
				this.mockMvc
						.perform(post(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
														   .replace("{option}", "dept"))
								.header(USERID_HEADER, userUuid)
								.header(AUTH_HEADER, auth)
								.contentType(APPLICATION_JSON_UTF8_VALUE)
								.content(departureTestData(LocationType.ICAO)))
						.andDo(print())
						.andExpect(status().isSeeOther())
						.andExpect(header().string("Location", not(isNull())))
						.andExpect(header().string("Location",startsWith(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
								   															  .replace("{option}/", ""))))
						.andReturn();
			String header = result.getResponse().getHeader("Location");
			String uuid = header.substring(LOCATION_SERVICE_NAME.replace("{gar_uuid}", garUuid)
						  									    .replace("{option}/", "")
						  									    .length());
			
			assertTrue(uuid.matches(REGEX_UUID));
		
			return uuid;
	}
	/*
	 * create new person for existing Gar
	 */
	
	public String createAPerson(UUID userUuid, String auth,String garUuid, String fileLocation) throws Exception{
		String location = readFileAsString(fileLocation);
		MvcResult result =
				this.mockMvc
				.perform(post(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))
						.header(USERID_HEADER, userUuid)
						.header(AUTH_HEADER, auth)
						.contentType(APPLICATION_JSON_UTF8_VALUE)
						.content(location))
						.andExpect(status().isSeeOther())
						.andExpect(header().string("Location", not(isNull())))
						.andExpect(header().string("Location",startsWith(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid))))
						.andReturn();
			String header = result.getResponse().getHeader("Location");
			String uuid = header.substring(PERSON_SERVICE_NAME.replace("{gar_uuid}", garUuid).length());
			
			assertTrue(uuid.matches(REGEX_UUID));
		
			return uuid;
	}
	
	
	
	
	/*
	 * Gets The GAR ID before each test to be included in URL
	 * @throws Exception
	 */
	public String retrieveGarUUID (UUID userUuid, String auth) throws Exception{
		JsonNode actualObj = getContentAsJsonNode(userUuid,auth,"/api/v1/WF/GARs/");
	    JsonNode garIdsNode = actualObj.get("gar_uuids");
	    //TODO assertThat(garIdsNode).as("Should be an array node here.").isNotNull();
	    
	    ArrayNode actualArr = (ArrayNode) garIdsNode ;
	    
	    String garUuid ;
	    if ( actualArr.size() == 0 )
	    {
	    	// There are no GAR to find
	    	garUuid = createAGar(userUuid, auth);
	    }
	    else
	    {
		    garUuid = actualArr.get(0).asText();
	    }
	    
//		String garUuid ="5231b533-ba17-4787-98a3-f2df37de2ad7";
				
		return garUuid;
	}
	
	public JsonNode getContentAsJsonNode(final UUID userUuid, final String auth,final String url)
			throws Exception, IOException, JsonProcessingException, UnsupportedEncodingException {
		
		MvcResult result = this.mockMvc
				.perform(get(url)
						.header(USERID_HEADER, userUuid)
						.header(AUTH_HEADER, auth))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode actualObj = getContentAsJsonNode(result);
		return actualObj;
	}
	
	private JsonNode getContentAsJsonNode(MvcResult result)
			throws IOException, JsonProcessingException, UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();
	    JsonNode actualObj = mapper.readTree(result.getResponse().getContentAsString());
		return actualObj;
	}
	
	
	
	/*//FIXME retrieveLocationUUID
	 * Gets The GAR ID before each test to be included in URL
	 * @throws Exception
	 */
	public String retrieveLocationUUID(UUID userUuid, String auth,String garUUID ) throws Exception{
		
		MvcResult result = this.mockMvc
				.perform(get(String.format("/api/v1/WF/GARs/%s/locations/",garUUID))
						.header(USERID_HEADER, userUuid)
						.header(AUTH_HEADER, auth))
				.andExpect(status().isOk())
				.andReturn();
		JsonNode actualObj = getContentAsJsonNode(result);
	    JsonNode locationIdsNode = actualObj.get("location_uuids");

	    ArrayNode actualArr = (ArrayNode) locationIdsNode ;
	    
	    String locationUUID;
	    
	    if ( actualArr.size() == 0 )
	    {
	    	// There are no GAR to find
	    	locationUUID = createALocation(userUuid,auth,garUUID);
	    }
	    else
	    {
	    	locationUUID = actualArr.get(0).asText();
	    }
	    
	    
		return locationUUID;
	}
	
	
	/*//FIXME retrievePersonUUID 
	 * Gets The GAR ID before each test to be included in URL
	 * @throws Exception
	 */	 
	
	private JsonNode retrievePeopleNode(UUID userUuid, String auth,String garUUID)
			throws Exception, IOException, JsonProcessingException, UnsupportedEncodingException {
		MvcResult result = this.mockMvc
				.perform(get(String.format("/api/v1/WF/GARs/%s/persons/",garUUID))
						.header(USERID_HEADER, userUuid)
						.header(AUTH_HEADER, auth))
				.andExpect(status().isOk())
				.andReturn();
		JsonNode actualObj = getContentAsJsonNode(result);
	    return actualObj.get("people");
	}
	
	public String retrieveCaptainUUID (UUID userUuid, String auth,String garUUID) throws Exception{

		JsonNode peopleNode = retrievePeopleNode(userUuid,auth,garUUID);
		
		String captainContent = peopleNode.get("captain").asText();
		String captainUuid ;
	    if ( captainContent == null )
	    {
	    	
	    	captainUuid = createAPerson(userUuid,auth,garUUID,"data/PersonData/CaptainTestData.json" );
	    	return captainUuid;
	    }
	    else{
	    	return captainContent;
	    }
	  
	}
	
	public String retrieveCrewUUID (UUID userUuid, String auth,String garUUID) throws Exception{

		JsonNode peopleNode = retrievePeopleNode(userUuid,auth,garUUID);
		JsonNode crewIdsNode = peopleNode.get("crew");
		    
		    ArrayNode actualArr = (ArrayNode) crewIdsNode ;
		    
		    String crewUuid ;
		    if ( actualArr.size() == 0 )
		    {
		    	// There are no GAR to find
		    	crewUuid = createAPerson(userUuid,auth,garUUID,"data/PersonData/CrewTestData.json");
		    }
		    else
		    {
			    crewUuid = actualArr.get(0).asText();
		    }
					
			return crewUuid;
	  
	}
	
	public String retrievePasengerUUID (UUID userUuid, String auth, String garUUID) throws Exception{

		JsonNode peopleNode = retrievePeopleNode(userUuid,auth,garUUID);
		JsonNode crewIdsNode = peopleNode.get("passengers");
		    
		    ArrayNode actualArr = (ArrayNode) crewIdsNode ;
		    
		    String passengerUuid ;
		    if ( actualArr.size() == 0 )
		    {
		    	// There are no GAR to find
		    	passengerUuid = createAPerson(userUuid,auth,garUUID,"data/PersonData/PasengerTestData.json");
		    }
		    else
		    {
		    	passengerUuid = actualArr.get(0).asText();
		    }
					
			return passengerUuid;
	  
	}
	
	
	/*
	 * Gets The file ID before each test to be included in URL
	 * @throws Exception
	 */	 
	public String retrieveFileUUID (UUID userUuid, String auth, String garUuid) throws Exception{
		MvcResult result = this.mockMvc
				.perform(get(String.format("/api/v1/WF/GARs/%s/files/", garUuid))
						.header(USERID_HEADER, userUuid)
						.header(AUTH_HEADER, auth))
				.andExpect(status().isOk())
				.andReturn();
		JsonNode actualObj = getContentAsJsonNode(result);
	    JsonNode fileIdsNode = actualObj.get("file_uuids");

	    ArrayNode actualArr = (ArrayNode) fileIdsNode ;
	    
	    String fileUuid = actualArr.get(0).asText();
				
		return fileUuid;
	}
	
	public final static String aircraftTestData() throws IOException{
		return readFileAsString("data/AircraftTestData/AircraftTestData.json") ;
	}
	
	public final static String arrivalTestData(LocationType type) throws IOException{
		String arrivalData = "data/LocationTestData/{type}ArrivalTestData.json".replace("{type}",type.getValue());
		return readFileAsString(arrivalData);
	}

	public final static String arrivalTestData(ZonedDateTime dateTime) throws IOException{
		String arrivalData = readFileAsString("data/LocationTestData/ArrivalTestDataWithDateTime.json");
		return arrivalData.replace("{arrival_date_time}", dateTime.toString());
	}
	
	public final static String departureTestData(LocationType type) throws IOException{
		String departureData = "data/LocationTestData/{type}DepartureTestData.json".replace("{type}",type.getValue());
		return readFileAsString(departureData);
	}
	public final static String departureTestData(ZonedDateTime dateTime) throws IOException{
		String departureData = readFileAsString("data/LocationTestData/DepartureTestDataWithDateTime.json");
		return departureData.replace("{departure_date_time}", dateTime.toString());
	}
	
	public final static String attributeTestData() throws IOException{
		return readFileAsString("data/AttributeTestData/AttributeTestData.json");
	}

	public final static String invalidAttributeTestData() throws IOException{
		return readFileAsString("data/AttributeTestData/MaxAttributeTestData.json");
	}
	
	public final static String personTestData(PersonType type) throws IOException{
		String data = "data/PersonData/{person}TestData.json".replace("{person}", type.getValue());
		return readFileAsString(data);
	}
	
	public final static String fileTestData() throws IOException{
		return  readFileAsString("data/FileTestData/FileTestData.json");
	}
	public final static String fileTestData(String type) throws IOException{
		String data = "data/FileTestData/{type}FileTestData.json".replace("{type}",	type);
		return  readFileAsString(data);
	}
	
	
	public static enum LocationType{
		ICAO("Icao"),
		POINT("Point"),
		IATA("Iata");
		
		private final String value;
		
		private LocationType(String value){
			this.value = value;
		}
		
		public String getValue(){
			return value;
		}
	}
	
	public static enum PersonType{
		CAPTAIN("Captain"),
		CREW("Crew"),
		PASSENGER("Passenger");
		
		private final String value;
		
		private PersonType(String value){
			this.value = value;
		}
		
		public String getValue(){
			return value;
		}
	}
}

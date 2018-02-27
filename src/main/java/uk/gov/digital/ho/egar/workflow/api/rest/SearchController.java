package uk.gov.digital.ho.egar.workflow.api.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.SearchRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarListResponse;
import uk.gov.digital.ho.egar.workflow.service.SearchService;

@RestController
@RequestMapping(WorkflowApi.ROOT_PATH_SEARCH)
@Api(consumes = MediaType.APPLICATION_JSON_VALUE,
	 produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController implements SearchRestService {
	
	/**
     * The person service.
     */
    @Autowired
    private SearchService searchService;
    
    
	@Override
	@ApiOperation(value = "Search existing people for a user.", notes = "Search existing people for a user.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, 
						 message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
						 response = PersonUUIDList.class),
			@ApiResponse(code = 400, message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND)})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = WorkflowApi.SEARCH_PERSON_SERVICE_NAME)
	public PersonUUIDList listOfExistingPeople(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
										   @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
										   @RequestParam(value = "search_criteria",required = false) String searchCriteria) throws WorkflowException{

		return searchService.searchPeople(new AuthValues(authToken, uuidOfUser),searchCriteria);
	}
	
	@Override
	@ApiOperation(value = "Search Existing Gars for a user.", notes = "Search Existing Gars for a user.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, 
						 message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
						 response = GarListResponse.class),
			@ApiResponse(code = 400, message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND)})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = WorkflowApi.SEARCH_GAR_SERVICE_NAME)
	public GarListResponse listOfExistingGars(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
											  @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
											  @RequestParam(value = "search_criteria",required = false) String searchCriteria) throws WorkflowException{

		return searchService.searchGars(new AuthValues(authToken, uuidOfUser),searchCriteria);
	}

}



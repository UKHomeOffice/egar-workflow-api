package uk.gov.digital.ho.egar.workflow.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.GarRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.GarNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarBulkSummaryResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSummary;
import uk.gov.digital.ho.egar.workflow.service.GarService;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;


/**
 * The gar controller that provides restful endpoints to access, create and modify an
 * existing general aviation reports.
 */
@RestController
@RequestMapping(WorkflowApi.GAR_SERVICE_NAME)
@Api(value = WorkflowApi.GAR_SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class GarController implements GarRestService {

    /**
     * The default logger for the class.
     */
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * The gar service
     */
    @Autowired
    private GarService garService;

    /**
     * The uri location utilities.
     */
    @Autowired
    private UriLocationUtilities uriLocationUtilities;

    /**
     * A get endpoint to retrieve a list of existing gars
     * @return a list of existing gars.
     */
    @ApiOperation(value = "Retrieve all existing GARs.",
            notes = "Retrieve a list of all General Aviation Reports for a user",
            response = GarList.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = GarList.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GarList getListOfGars(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    									 @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser) throws WorkflowException{
    	LOGGER.debug("Retrieving a list of GARs linked to user");
        return garService.getAllGars(new AuthValues(authToken, uuidOfUser)); 
    }

    /**
     * A post endpoint to create a new gar
     * @return The 303 response
     * @throws URISyntaxException When a redirection url cannot be created.
     * @throws InvalidUserException 
     */
    @Override
    @ApiOperation(value = "Create a new GAR.",
            notes = "Create a new General Aviation Report for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGAR(@RequestHeader(AuthValues.AUTH_HEADER) String authToken, 
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser) throws WorkflowException {
        UUID garUuid = garService.createGar(new AuthValues(authToken, uuidOfUser));

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createGarURI(garUuid);
 
        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
    }

    /**
     * A get endpoint that retrieves an existing gar, in summary format
     * @param garId the gar uuid.
     * @return The gar summary response
     * @throws GarNotFoundWorkflowException 
     */
    @Override
    @ApiOperation(value = "Retrieve a GAR summary.",
            notes = "Retrieve an existing General Aviation Report summary for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = GarSummary.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = WorkflowApi.PATH_GAR_SUMMARY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GarSummary retrieveGarSummary(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    									 @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    									 @PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {
        return garService.getGarSummary(new AuthValues(authToken, uuidOfUser),garId);
    }

    /**
     * A get endpoint that retrieves an existing gar
     * @param garId the gar uuid.
     * @return The gar summary response
     */
    @Override
    @ApiOperation(value = "Retrieve a GAR.",
            notes = "Retrieve an existing General Aviation Report for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = GarSkeleton.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = WorkflowApi.PATH_GAR_IDENTIFIER,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GarSkeleton retrieveGAR(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    							   @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    							   @PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {

        return garService.getGar(new AuthValues(authToken, uuidOfUser), garId);

    }

    /**
     * A get endpoint that bulk retrieves a list of GARs
     * -------------------------------------------------------------------------------------------
     * @throws WorkflowException 
     */
    
    
    @Override
    @ApiOperation(value = "Bulk retrieve a list of GAR summaries.",
            notes = "Retrieve a list of existing General Aviation Report in summary for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = GarBulkSummaryResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = WorkflowApi.PATH_BULK,
    			consumes = MediaType.APPLICATION_JSON_VALUE,
           		produces = MediaType.APPLICATION_JSON_VALUE)
    public GarBulkSummaryResponse bulkRetrieveGARs(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    									   @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    									   @RequestBody GarList garList) throws WorkflowException{
    	
    	return garService.getBulkGars(new AuthValues(authToken, uuidOfUser),garList.getGarIds());
    }
    
}


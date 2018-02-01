package uk.gov.digital.ho.egar.workflow.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.LocationRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.BadRequestWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Location;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationResponse;
import uk.gov.digital.ho.egar.workflow.service.LocationService;
import uk.gov.digital.ho.egar.workflow.model.rest.response.LocationListResponse;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import java.net.URI;
import java.util.UUID;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.LOCATION_IDENTIFIER;

@RestController
@RequestMapping(WorkflowApi.LOCATION_SERVICE_NAME)
@Api(value = WorkflowApi.LOCATION_SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class LocationController implements LocationRestService {
	
	@Autowired
	private LocationService locationService;

    /**
     * The uri location utilities.
     */
    @Autowired
    private UriLocationUtilities uriLocationUtilities;

	/**
	 * ----------------------------------------------------------------------------------------------
	 */
	@Override
	@ApiOperation(value = "Retrieve all locations for a GAR.",
            notes = "Retrieve all locations for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = LocationListResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationListResponse retrieveAllLocations(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) UUID garId) throws WorkflowException {
        return locationService.retrieveAllLocations(new AuthValues(authToken, uuidOfUser), garId);
    }
	
	/**
	 * ----------------------------------------------------------------------------------------------
	 */
	@Override
	@ApiOperation(value = "Add/amend the departure location for a GAR.",
            notes = "Add/amend the departure location for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = LocationResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    		})
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(path = WorkflowApi.PATH_LOCATION_DEPARTURE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateDepartureLocation
    	  (
    		@RequestHeader(AuthValues.AUTH_HEADER) 		String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) 	UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) 		UUID garId, 
    		@RequestBody 								Location location) 
    				throws WorkflowException {
		
        UUID locationUuid = locationService.updateDepartureLocation(new AuthValues(authToken, uuidOfUser), garId, location);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createLocationUri(garId, locationUuid);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }

	/**
	 * ----------------------------------------------------------------------------------------------
	 */
	@Override
	@ApiOperation(value = "Add/amend the arrival location for a GAR.",
            notes = "Add/amend the arrival location for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = LocationResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(path = WorkflowApi.PATH_LOCATION_ARRIVAL,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateArrivalLocation(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId, 
    		@RequestBody final Location location) throws WorkflowException {
        UUID locationUuid =  locationService.updateArrivalLocation(new AuthValues(authToken, uuidOfUser), garId, location);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createLocationUri(garId, locationUuid);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }

	@Override
	@ApiOperation(value = "Retrieve a location for a GAR.",
            notes = "Retrieve a location for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = LocationResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = WorkflowApi.PATH_LOCATION_IDENTIFIER,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationResponse retrieveSingleLocation(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId, 
    		@PathVariable(value = LOCATION_IDENTIFIER) final UUID locationId) throws WorkflowException {
        return locationService.retrieveSingleLocation(new AuthValues(authToken, uuidOfUser), garId, locationId);
    }

    @Override
	@ApiOperation(value = "Retrieve the departure location for a GAR.",
            notes = "Retrieve the departure location for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = LocationResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = WorkflowApi.PATH_LOCATION_DEPARTURE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationResponse retrieveDeptLocation(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {
        return locationService.retrieveDepartureLocation(new AuthValues(authToken, uuidOfUser), garId);
    }

    @Override
	@ApiOperation(value = "Retrieve the arrival location for a GAR.",
            notes = "Retrieve the arrival location for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = LocationResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = WorkflowApi.PATH_LOCATION_ARRIVAL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationResponse retrieveArrvLocation(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {
        return locationService.retrieveArrivalLocation(new AuthValues(authToken, uuidOfUser), garId);
    }
    
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody WorkflowException handleException(HttpMessageNotReadableException ex) {
    	// Don't send the raw request or you get a 'Direct self-reference leading to cycle' error.
        return new BadRequestWorkflowException(ex.getMessage()); 
    }
}


package uk.gov.digital.ho.egar.workflow.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.AircraftRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Aircraft;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AircraftResponse;
import uk.gov.digital.ho.egar.workflow.service.AircraftService;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * The REST end-point for aircraft operations in the work-flow.
 */
@RestController
@RequestMapping(WorkflowApi.AIRCRAFT_SERVICE_NAME)
@Api(value = WorkflowApi.AIRCRAFT_SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class AircraftController implements AircraftRestService
{

	@Autowired
	private AircraftService aircraftService;

    /**
     * The uri location utilities.
     */
    @Autowired
    private UriLocationUtilities uriLocationUtilities;

    /* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.api.rest.AircraftService#createAircraft(java.lang.String, uk.gov.digital.ho.egar.workflow.model.rest.Aircraft)
	 */
    /* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.api.rest.AircraftRestService#createAircraft(java.util.UUID, uk.gov.digital.ho.egar.workflow.model.rest.Aircraft)
	 */
    @Override
	@ApiOperation(value = "Create/update the aircraft for a GAR.",
            notes = "Create or update the aircraft for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_UPDATE_REDIRECT_KEY),
            @ApiResponse(
                    code = 400,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createAircraft(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId,
    		@RequestBody final Aircraft aircraft) throws WorkflowException, RestClientException, URISyntaxException, SolrServerException, IOException {

        aircraftService.createAircraft(new AuthValues(authToken, uuidOfUser),garId, aircraft);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createAircraftUri(garId);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }


    /* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.api.rest.AircraftRestService#retrieveAircraft(java.util.UUID)
	 */
    @Override
	@ApiOperation(value = "Retrieve the aircraft for a GAR.",
            notes = "Retrieve the aircraft for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = AircraftResponse.class),
            @ApiResponse(
                    code = 400,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public AircraftResponse retrieveAircraft(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {
        return aircraftService.retrieveAircraft(new AuthValues(authToken, uuidOfUser),garId);
    }
     
}


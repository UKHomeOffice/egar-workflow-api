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
import org.springframework.web.bind.annotation.*;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.PersonRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.GarNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PeopleSkeletonResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonResponse;
import uk.gov.digital.ho.egar.workflow.service.PersonService;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_PERSON_IDENTIFIER;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.PERSON_IDENTIFIER;

/**
 * The person controller that provides restful endpoints to access create and modify a
 *  general aviation report.
 */
@RestController
@RequestMapping(WorkflowApi.PERSON_SERVICE_NAME)
@Api(value = WorkflowApi.PERSON_SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController implements PersonRestService {

    /**
     * The uri location utilities.
     */
    @Autowired
    private UriLocationUtilities uriLocationUtilities;

    /**
     * The person service.
     */
    @Autowired
    private PersonService personService;

    /**
     * A get endpoint that retrieves a list of person uuids on an existing gar
     * @param garId The gar uuid.
     * @return The list of gars.
     * @throws GarNotFoundWorkflowException 
     */
    @Override
    @ApiOperation(value = "Retrieve the list of people for a GAR.",
            notes = "Retrieve a list of people for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = PeopleSkeletonResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PeopleSkeletonResponse getAllPersons(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {
        return personService.getAllPersons(new AuthValues(authToken, uuidOfUser), garId);
    }

    /**
     * A post endpoint that adds a new person to an existing gar.
     * @param garId The gar uuid.
     * @param person The person to add
     * @return The 303 response
     * @throws URISyntaxException When a redirection url cannot be created.
     */
    @Override
    @ApiOperation(value = "Add a new person to a GAR.",
            notes = "Add a new person to an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_UPDATE_REDIRECT_KEY),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addNewPerson(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) UUID garId, 
    		@Valid @RequestBody Person person) throws WorkflowException {
        UUID personId = personService.addNewPerson(new AuthValues(authToken, uuidOfUser), garId, person);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createPersonURI(garId, personId);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }
    /**
     * A post endpoint that updates an existing person to an existing gar.
     * @param garId The gar uuid.
     * @param personId The person uuid.
     * @param person The person to add
     * @return The 303 response
     * @throws URISyntaxException When a redirection url cannot be created.
     */
    @Override
    @ApiOperation(value = "Update a person on a GAR.",
            notes = "Update an existing person to an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(path = PATH_PERSON_IDENTIFIER,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePerson(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId,
    		@PathVariable(value = PERSON_IDENTIFIER) final UUID personId,
    		@Valid @RequestBody final Person person) throws WorkflowException {
    	
        personService.updatePerson(new AuthValues(authToken, uuidOfUser), garId, personId, person);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createPersonURI(garId, personId);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
    }

    /**
     * A get endpoint that retrieves an existing person from an existing gar
     * @param garId the gar uuid.
     * @param personId the person uuid.
     * @return The existing person.
     */
    @Override
    @ApiOperation(value = "Retrieve a person from a GAR.",
            notes = "Retrieve an existing person from an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = PersonResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = PATH_PERSON_IDENTIFIER,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonResponse getPerson(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId,
    		@PathVariable(value = PERSON_IDENTIFIER) final UUID personId) throws WorkflowException {
        return personService.getPerson(new AuthValues(authToken, uuidOfUser), garId, personId);
    }

    /**
     * A delete endpoint that deletes an existing person from an existing gar
     * @param garId the gar uuid.
     * @param personId the person uuid.
     * @return The response.
     */
    @Override
    @ApiOperation(value = "Delete a person from a GAR.",
            notes = "Delete an existing person from an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = PersonResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(path = PATH_PERSON_IDENTIFIER)
    public ResponseEntity<Void> deletePerson(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) UUID garId, 
    		@PathVariable(value = PERSON_IDENTIFIER) UUID personId) throws WorkflowException {
        personService.deletePerson(new AuthValues(authToken, uuidOfUser), garId, personId);
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

}


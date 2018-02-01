package uk.gov.digital.ho.egar.workflow.api.rest;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.workflow.api.SubmissionRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.DataNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.GarNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.SubmissionAlreadyExistsException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.response.SubmissionGar;
import uk.gov.digital.ho.egar.workflow.service.SubmissionService;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;
/**
 * The Submission controller that provides restful endpoints to access and submit an
 * existing general aviation reports.
 */
@RestController
@RequestMapping(WorkflowApi.SUBMISSION_SERVICE_NAME)
@Api(value = WorkflowApi.SUBMISSION_SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class SubmissionController implements SubmissionRestService { 

	/**
	 * The submission service
	 */
	@Autowired
	private SubmissionService submissionService;
	
	/**
	 * The uri location utilities.
	 */
	@Autowired
	private UriLocationUtilities uriLocationUtilities;

	/**
	 * A get endpoint that retrieves submission details of a submitted gar
	 * @param garUuid The gar uuid.
	 * @return The submission details.
	 * @throws GarNotFoundWorkflowException 
	 */
	@Override
	@ApiOperation(value = "Retrieve a submitted GAR.",
	notes = "Retrieve a submitted existing General Aviation Report, for a user")
	@ApiResponses(value = {
			@ApiResponse(
					code = 200,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
					response = SubmissionGar.class),
			@ApiResponse(
					code = 400,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
			@ApiResponse(
					code = 401,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
	})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public SubmissionGar retrieveSubmission(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
			@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
			@PathVariable(value = GAR_IDENTIFIER) UUID garUuid) throws WorkflowException{

		return submissionService.retrieveSubmission(new AuthValues(authToken, uuidOfUser), garUuid);
	}

	/**
	 * A post endpoint that submits a gar
	 * @param garUuid The gar uuid.
	 * @return The 303 response
	 * @throws DataNotFoundWorkflowException 
	 * @throws URISyntaxException 
	 * @throws SubmissionAlreadyExistsException 
	 */
	@Override
	@ApiOperation(value = "Submit a GAR.",
	notes = "Submit an existing General Aviation Report, for a user")
	@ApiResponses(value = {
			@ApiResponse(
					code = 303,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY),
			@ApiResponse(
					code = 400,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
			@ApiResponse(
					code = 401,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED),
			@ApiResponse(
					code = 403,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_FORBIDDEN)
	})
	@ResponseStatus(HttpStatus.SEE_OTHER)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> submit( @RequestHeader(value = UserValues.AUTH_HEADER, required = true) String authHeader,
										@RequestHeader(value = UserValues.USERID_HEADER, required = true) UUID uuidOfUser,
							            @RequestHeader(value = UserValues.FORENAME_HEADER, required = false) String forename,
							            @RequestHeader(value = UserValues.SURNAME_HEADER, required = false) String surname,
							            @RequestHeader(value = UserValues.EMAIL_HEADER, required = true) String email,
							            @RequestHeader(value = UserValues.CONTACT_HEADER, required = false) String contact,
							            @RequestHeader(value = UserValues.ALTERNATIVE_CONTACT_HEADER, required= false) String altContact,
							            @PathVariable(value = GAR_IDENTIFIER) UUID garUuid) throws WorkflowException {
		
		submissionService.submit(new UserValues(authHeader,uuidOfUser, email, forename, surname, contact, altContact), garUuid);	

		//Creating the redirection URI
		URI redirectLocation = uriLocationUtilities.createSubmissionURI(garUuid);

		//Creating response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(redirectLocation);

		return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

	}

	/**
	 * A post endpoint that cancels submission of a gar
	 * @param garUuid The gar uuid.
	 * @return The 303 response
	 * @throws DataNotFoundWorkflowException
	 * @throws URISyntaxException
	 * @throws SubmissionAlreadyExistsException
	 */
	@Override
	@ApiOperation(value = "Cancel a submitted GAR.",
			notes = "Cancel submission of an existing General Aviation Report, for a user")
	@ApiResponses(value = {
			@ApiResponse(
					code = 303,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY),
			@ApiResponse(
					code = 400,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
			@ApiResponse(
					code = 401,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED),
			@ApiResponse(
					code = 403,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_FORBIDDEN)
	})
	@ResponseStatus(HttpStatus.SEE_OTHER)
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> cancel( @RequestHeader(value = UserValues.AUTH_HEADER, required = true) String authHeader,
										@RequestHeader(value = UserValues.USERID_HEADER, required = true) UUID uuidOfUser,
										@RequestHeader(value = UserValues.FORENAME_HEADER, required = false) String forename,
										@RequestHeader(value = UserValues.SURNAME_HEADER, required = false) String surname,
										@RequestHeader(value = UserValues.EMAIL_HEADER, required = true) String email,
										@RequestHeader(value = UserValues.CONTACT_HEADER, required = false) String contact,
										@RequestHeader(value = UserValues.ALTERNATIVE_CONTACT_HEADER, required= false) String altContact,
										@PathVariable(value = GAR_IDENTIFIER) UUID garUuid) throws WorkflowException {

		submissionService.cancel(new UserValues(authHeader,uuidOfUser, email, forename, surname, contact, altContact), garUuid);

		//Creating the redirection URI
		URI redirectLocation = uriLocationUtilities.createSubmissionURI(garUuid);

		//Creating response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(redirectLocation);

		return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

	}

}



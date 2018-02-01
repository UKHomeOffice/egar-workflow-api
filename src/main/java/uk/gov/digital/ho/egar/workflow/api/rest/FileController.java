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
import uk.gov.digital.ho.egar.workflow.api.FileRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileIdWithGarResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FilesListGarResponse;
import uk.gov.digital.ho.egar.workflow.service.FileService;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_FILE_IDENTIFIER;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.FILE_IDENTIFIER;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;

import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

/**
 * The file controller that provides restful endpoints to submit and retrieve a file for a
 *  general aviation report.
 */
@RestController
@RequestMapping(WorkflowApi.FILE_SERVICE_NAME)
@Api(value = WorkflowApi.FILE_SERVICE_NAME, 
	 produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController implements FileRestService {
	
	 /**
     * The uri location utilities.
     */
    @Autowired
    private UriLocationUtilities uriLocationUtilities;
    
	/**
     * The file service.
     */
	@Autowired
	private FileService fileService;
	
	/**
	* A post endpoint to submit a file for an existing gar
	* @param garUuid gar uuid
	* @param fileDetails Uploaded file
	* @return The 303 response
	*/
	@Override
	@ApiOperation(value = "Submit a File.",
	notes = "Submit a file for an existing General Aviation Report, for a user")
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
	public ResponseEntity<Void> uploadFile(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    									   @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    									   @PathVariable(value = GAR_IDENTIFIER) UUID garUuid,
    									   @Valid @RequestBody final FileDetails fileDetails) throws WorkflowException {
		UUID fileUuid = fileService.uploadFileDetails(new AuthValues(authToken, uuidOfUser), garUuid, fileDetails);

		//Creating the redirection location URI
		URI redirectLocation = uriLocationUtilities.createFileURI(garUuid, fileUuid);

		//Creating the response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(redirectLocation);

		return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

		
	}
	
	/**
	 * A get endpoint that retrieves an list of files for an existing gar
	 * @param garUuid the gar uuid.
	 * @return The file list response
	 */

    @Override
	@ApiOperation(value = "Retrieve a list of uploaded files for a gar.",
            notes = "Retrieve a list of existing files for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = FilesListGarResponse.class),
            @ApiResponse(
					code = 400,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public FilesListGarResponse retrieveFileList(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
												 @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
												 @PathVariable(value = GAR_IDENTIFIER) UUID garUuid) throws WorkflowException {
        return fileService.getAllFiles(new AuthValues(authToken, uuidOfUser), garUuid);
    }
    
    /**
     * A get endpoint that retrieves an existing file from an existing gar
     * @param garUuid the gar uuid.
     * @param fileUuid the file uuid.
     * @return The existing person.
     */
    @Override
	@ApiOperation(value = "Retrieve a file for a GAR.",
            notes = "Retrieve an existing files for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = FileIdWithGarResponse.class),
            @ApiResponse(
					code = 400,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_NOT_FOUND),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = PATH_FILE_IDENTIFIER,
    			produces = MediaType.APPLICATION_JSON_VALUE)
    public FileIdWithGarResponse retrieveFile(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    										@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    										@PathVariable(value = GAR_IDENTIFIER) UUID garUuid, 
    										@PathVariable(value = FILE_IDENTIFIER) UUID fileUuid) throws WorkflowException {
        return fileService.getFile(new AuthValues(authToken, uuidOfUser), garUuid, fileUuid);
    }
    
    /**
     * A delete endpoint that deletes an existing file from an existing gar
     * @param garUuid the gar uuid.
     * @param fileUuid the file uuid.
     * @return The response.
     */
    @Override
	@ApiOperation(value = "Delete a file for a GAR.",
            notes = "Delete an existing files for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = FileIdWithGarResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED),
            @ApiResponse(
					code = 403,
					message = WorkflowApiResponse.SWAGGER_MESSAGE_FORBIDDEN)
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(path = PATH_FILE_IDENTIFIER,
    			   produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteFile(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
			 							   @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
			 							   @PathVariable(value = GAR_IDENTIFIER) UUID garUuid, 
			 							   @PathVariable(value = FILE_IDENTIFIER) UUID fileUuid) throws WorkflowException {
    	fileService.deleteFile(new AuthValues(authToken, uuidOfUser), garUuid, fileUuid);
    	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

}





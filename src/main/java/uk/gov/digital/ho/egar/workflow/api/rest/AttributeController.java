package uk.gov.digital.ho.egar.workflow.api.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.AttributeRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.Attribute;
import uk.gov.digital.ho.egar.workflow.model.rest.response.AttributeResponse;
import uk.gov.digital.ho.egar.workflow.service.AttributeService;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.GAR_IDENTIFIER;

/**
 * The attribute controller that provides restful endpoints to access and modify an
 * attribute on an existing general aviation report.
 */
@RestController
@RequestMapping(WorkflowApi.ATTRIBUTE_SERVICE_NAME)
@Api(value = WorkflowApi.ATTRIBUTE_SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class AttributeController implements AttributeRestService {

    /**
     * The default logger for the class.
     */
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * The attribute service.
     */
    @Autowired
    private AttributeService attributeService;

    /**
     * The uri location utilities.
     */
    @Autowired
    private UriLocationUtilities uriLocationUtilities;

    /**
     * A post endpoint to add or amend the attribute for a GAR.
     * @param garId The gar uuid
     * @param attribute The updated attribute value
     * @param request The http request object
     * @return A response entity
     * @throws URISyntaxException When an redirection uri cannot be created.
     */
    @Override
    @ApiOperation(value = "Add/amend the attribute for a GAR.",
            notes = "Add/amend the attribute for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 301,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_UPDATE_REDIRECT_KEY),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postAttribute(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
                                              @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
                                              @PathVariable(value = GAR_IDENTIFIER) UUID garId,
                                              @RequestBody @Valid Attribute attribute,
                                              Errors errors) throws WorkflowException {

        if (errors.hasErrors()){
            return new ResponseEntity(new ApiErrors(errors), HttpStatus.BAD_REQUEST);
        }

        attributeService.updateAttribute(new AuthValues(authToken, uuidOfUser), garId, attribute);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.createAttributeURI(garId);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }

    /**
     * Gets the attribute for a general aviation report.
     * @param garId The gar uuid
     * @return The attribute response
     */
    @Override
    @ApiOperation(value = "Retrieve the attribute for a GAR.",
            notes = "Retrieve the attribute for an existing General Aviation Report, for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = AttributeResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public AttributeResponse getAttribute(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    		@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser,
    		@PathVariable(value = GAR_IDENTIFIER) final UUID garId) throws WorkflowException {
        AttributeResponse response;
        try {
             response = attributeService.retrieveAttributes(new AuthValues(authToken, uuidOfUser), garId);
        }catch(final Exception ex){
            LOGGER.error(String.format("Unable to retrieve attribute for gar_uuid='%s'",garId), ex);
            throw ex;
        }
        return response;
    }

    public static class ApiErrors {

        @JsonProperty("message")
        private final List<String> errorMessages = new ArrayList<>();

        public ApiErrors(Errors errors) {
            for(final FieldError error : errors.getFieldErrors()){
                errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
            }
            for(final ObjectError error : errors.getGlobalErrors()){
                errorMessages.add(error.getObjectName() + ": " + error.getDefaultMessage());
            }
        }
    }
}


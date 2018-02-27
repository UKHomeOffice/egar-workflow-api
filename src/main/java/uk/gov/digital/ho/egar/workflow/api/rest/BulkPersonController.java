package uk.gov.digital.ho.egar.workflow.api.rest;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.BulkPersonRestService;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApi;
import uk.gov.digital.ho.egar.workflow.api.WorkflowApiResponse;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PeopleBulkResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.PersonUUIDList;
import uk.gov.digital.ho.egar.workflow.service.PersonService;

/**
 * The person controller that provides restful endpoints to access create and modify a
 *  general aviation report.
 */
@RestController
@RequestMapping(WorkflowApi.ROOT_PATH_BULK)
@Api(value = WorkflowApi.ROOT_PATH_BULK, produces = MediaType.APPLICATION_JSON_VALUE)
public class BulkPersonController implements BulkPersonRestService {
	private static Log logger = LogFactory.getLog(BulkPersonController.class); 
    /**
     * The person service.
     */
    @Autowired
    private PersonService personService;
    
    /* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.workflow.api.rest.BulkPersonRestService#bulkRetrievePeople(java.lang.String, java.util.UUID, java.util.List)
	 */

    @Override
	@ApiOperation(value = "Bulk retrieve a list of Person Details.",
            notes = "Retrieve a list of person details for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = PeopleBulkResponse.class),
            @ApiResponse(
                    code = 401,
                    message = WorkflowApiResponse.SWAGGER_MESSAGE_UNAUTHORISED)
    })

    @ResponseStatus(HttpStatus.OK)
    @PostMapping( path = WorkflowApi.PATH_BULK_PERSON,
    		    consumes = MediaType.APPLICATION_JSON_VALUE,
           		produces = MediaType.APPLICATION_JSON_VALUE)
    public PeopleBulkResponse bulkRetrievePeople(@RequestHeader(AuthValues.AUTH_HEADER) String authToken,
    									 @RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    									 @RequestBody PersonUUIDList peopleUuids) throws WorkflowException{
    	logger.info("Bulk request of people");
    	logger.info("Uuid of requesting person: "+ uuidOfUser!=null?uuidOfUser.toString(): "Uuid of user is null");
    	
    	logger.info("Number of uuids in request list: "+ 
    			peopleUuids!=null && peopleUuids.getPersonUuids() != null ? peopleUuids.getPersonUuids().size(): " peopleUuids or list of uuids in request null" );
    	return personService.getBulkPeople(new AuthValues(authToken, uuidOfUser),peopleUuids.getPersonUuids());
    }

}

package uk.gov.digital.ho.egar.workflow.api;

import uk.gov.digital.ho.egar.constants.ServicePathConstants;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApiUriParameter.*;

public interface WorkflowApi {
    public static final String ROOT_SERVICE_NAME = "WF";
    public static final String ROOT_PATH =
            ServicePathConstants.ROOT_PATH_SEPERATOR +
                    ServicePathConstants.ROOT_SERVICE_API +
                    ServicePathConstants.ROOT_PATH_SEPERATOR +
                    ServicePathConstants.SERVICE_VERSION_ONE +
                    ServicePathConstants.ROOT_PATH_SEPERATOR +
                    ROOT_SERVICE_NAME;

    public static final String GAR_SERVICE_NAME = ROOT_PATH + "/GARs"+ServicePathConstants.ROOT_PATH_SEPERATOR ;
    public static final String PATH_GAR_IDENTIFIER = "{"+GAR_IDENTIFIER+"}"+ServicePathConstants.ROOT_PATH_SEPERATOR;
    public static final String PATH_GAR_SUMMARY = PATH_GAR_IDENTIFIER + "summary";
    
    public static final String AIRCRAFT_SERVICE_NAME =  GAR_SERVICE_NAME + PATH_GAR_IDENTIFIER + "aircraft" 
    													+ServicePathConstants.ROOT_PATH_SEPERATOR;

    public static final String LOCATION_SERVICE_NAME = GAR_SERVICE_NAME + PATH_GAR_IDENTIFIER + "locations" 
    													+ServicePathConstants.ROOT_PATH_SEPERATOR;
    public static final String PATH_LOCATION_IDENTIFIER = "{"+LOCATION_IDENTIFIER+"}";
    public static final String PATH_LOCATION_DEPARTURE= "dept" + ServicePathConstants.ROOT_PATH_SEPERATOR;
    public static final String PATH_LOCATION_ARRIVAL= "arr"+ ServicePathConstants.ROOT_PATH_SEPERATOR;

    public static final String ATTRIBUTE_SERVICE_NAME = GAR_SERVICE_NAME + PATH_GAR_IDENTIFIER + "attributes"
    													+ServicePathConstants.ROOT_PATH_SEPERATOR;

    public static final String PERSON_SERVICE_NAME = GAR_SERVICE_NAME + PATH_GAR_IDENTIFIER + "persons"
    													+ServicePathConstants.ROOT_PATH_SEPERATOR;
    public static final String PATH_PERSON_IDENTIFIER = "{"+PERSON_IDENTIFIER+"}" +ServicePathConstants.ROOT_PATH_SEPERATOR;
   
    public static final String FILE_SERVICE_NAME = GAR_SERVICE_NAME + PATH_GAR_IDENTIFIER + "files"
    												+ServicePathConstants.ROOT_PATH_SEPERATOR;
    public static final String PATH_FILE_IDENTIFIER = "{"+FILE_IDENTIFIER+"}" +ServicePathConstants.ROOT_PATH_SEPERATOR;

    public static final String SUBMISSION_SERVICE_NAME = GAR_SERVICE_NAME + PATH_GAR_IDENTIFIER + "Submission"
    													+ServicePathConstants.ROOT_PATH_SEPERATOR;

    public static final String PATH_CANCEL_SUBMISSION = "cancellation";
    
    public static final String PATH_BULK = "summaries";
    
    public static final String PATH_SEARCH = "search";
    
    public static final String ROOT_PATH_SEARCH = ROOT_PATH +  ServicePathConstants.ROOT_PATH_SEPERATOR + PATH_SEARCH;
    
    public static final String SEARCH_PERSON_SERVICE_NAME =  ServicePathConstants.ROOT_PATH_SEPERATOR  + "persons" 
    														+ ServicePathConstants.ROOT_PATH_SEPERATOR;
    
    public static final String SEARCH_GAR_SERVICE_NAME = 	 ServicePathConstants.ROOT_PATH_SEPERATOR + "GARs" 
															+ ServicePathConstants.ROOT_PATH_SEPERATOR;
    
    public static final String ROOT_PATH_BULK =  ROOT_PATH +  ServicePathConstants.ROOT_PATH_SEPERATOR + PATH_BULK;
    
    public static final String PATH_BULK_PERSON = ServicePathConstants.ROOT_PATH_SEPERATOR + "persons" + ServicePathConstants.ROOT_PATH_SEPERATOR ;
}

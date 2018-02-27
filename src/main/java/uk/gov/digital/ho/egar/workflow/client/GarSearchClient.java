package uk.gov.digital.ho.egar.workflow.client;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.GarSearchDetails;

public interface GarSearchClient {
	public static String SOLR_QUERY_GAR_STRING_KEY 	     = "${egar.gar.search.query}";
	public static String SOLR_GAR_CORE_KEY 				 = "${egar.gar.search.core}";
	
	static final String SOLR_MANDATORY_FIELD_IN_QUERY 	 = "+";
	static final String SOLR_OPEN_MULTIMATCH_SECTION	 = "(";
	static final String SOLR_CLOSE_MULTIMATCH_SECTION 	 = ")";
	static final String SOLR_MULTIMATCH_KEY 			 = "*";
	static final String SOLR_QUERY_FIELD_SPACER_KEY 	 = ":";

	static final String SOLR_USER_UUID_KEY 				 = "user_uuid"; 
	static final String SOLR_GAR_REF_KEY 				 = "gar_uuid";
	static final String SOLR_AIRCRAFT_REG_KEY 			 = "aircraft_registration";
	static final String SOLR_LOCATION_DEPARTURE_KEY 	 = "location_ICAO";
	static final String SOLR_LOCATION_DATE_KEY 			 = "date_of_departure";
	
	static final String SOLR_HEALTH_URL_POSTFIX 		 = "/admin/cores?wt=json&action=STATUS";
	 
	/**
	 * Add gar to search index 
	 * @param authValues User details for authorisation with search service
	 * @param garDetails parameters that are searched upon
	 * @throws WorkflowException indicates that process could not be performed. Exception maybe a derived class.
	 */
	public void addGarToIndex(final AuthValues authValues,
							  final GarSearchDetails garDetails,
							  final boolean updateLocation,
							  final boolean updateAircraft) throws WorkflowException ;
	/**
	 * Retrieves list of gar Uuids matching search criteria
	 * @param authValues User details for authorisation with search service
	 * @param searchString Search criteria e.g. "REG"
	 * @return List of matching gar uuids
	 * @throws WorkflowException indicates that process could not be performed. Exception maybe a derived class.
	 */
	
	public List<UUID> findMatchingGars(final AuthValues authValues,
									   final String searchString) throws WorkflowException;
}

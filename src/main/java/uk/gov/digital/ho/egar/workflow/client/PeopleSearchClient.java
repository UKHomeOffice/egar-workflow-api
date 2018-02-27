package uk.gov.digital.ho.egar.workflow.client;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.model.rest.PeopleSearchDetails;

public interface PeopleSearchClient {
	
	public static String SOLR_QUERY_PEOPLE_STRING_KEY = "${egar.people.search.query}";
	public static String SOLR_PEOPLE_CORE_KEY = "${egar.people.search.core}";
	
	public static final String SOLR_MANDATORY_FIELD_IN_QUERY = "+";
	public static final String SOLR_OPEN_MULTIMATCH_SECTION = "(";
	public static final String SOLR_CLOSE_MULTIMATCH_SECTION = ")";
	public static final String SOLR_MULTIMATCH_KEY = "*";
	public static final String SOLR_QUERY_FIELD_SPACER_KEY = ":";
	public static final String SOLR_LASTNAME_REG_KEY = "lastname";
	public static final String SOLR_FORENAME_KEY = "forename";
	public static final String SOLR_PERSON_REF_KEY = "person_uuid";
	public static final String SOLR_USER_UUID_KEY = "user_uuid"; 
	
	static final String SOLR_HEALTH_URL_POSTFIX = "/admin/cores?wt=json&action=STATUS";
	
	/**
	 * Add person to search index
	 * @param authValues
	 * @param peopleDetails
	 * @throws WorkflowException
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void addPersonToIndex(final AuthValues authValues,
                                            final PeopleSearchDetails peopleDetails) throws WorkflowException;
	
	/**
	 * Retrieves list of persons gar UUIDs matching search criteria 
	 * @param authValues
	 * @param searchString
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public List<UUID> findMatchingPeople(final AuthValues authValues, String searchString) throws WorkflowException;
}
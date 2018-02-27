package uk.gov.digital.ho.egar.workflow.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.DetailBuilder;
import uk.gov.digital.ho.egar.workflow.client.PeopleSearchClient;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.PeopleSearchDetails;

@Component
@Profile({"!mock-person-search"})
public class PersonSearchRestClient implements DataClient<PeopleSearchClient>,PeopleSearchClient{
	private static final Logger logger = LoggerFactory.getLogger(PersonSearchRestClient.class);

	private DetailBuilder detailBuilder;
	private WorkflowPropertiesConfig config;
	private SolrClient solrClient;
	private SolrClient solrUpdateClient;
	private String solrCore;
	
	
	


	public PersonSearchRestClient(@Autowired DetailBuilder details, 
			@Autowired WorkflowPropertiesConfig cfg, 
			@Value(SOLR_PEOPLE_CORE_KEY) String core) {
		logger.debug("Constructor");
		try {
			solrCore = core;
			detailBuilder = details;
			this.config = cfg;

			solrClient  = new HttpSolrClient.Builder().withBaseSolrUrl(cfg.getGetSolrApiUrl()).build();
			solrUpdateClient  = 
					new HttpSolrClient.Builder().withBaseSolrUrl(config.getGetSolrApiUrl()+ "/"+ solrCore).build();
		} catch (Exception e) {
			logger.error("Milton" + e.getLocalizedMessage());
		}
	}



	@Override
	public void contribute(Builder builder) {
		detailBuilder.withDetail(this,builder);

	}

	@Override
	public void addPersonToIndex(AuthValues authValues, PeopleSearchDetails peopleDetails) 
			throws WorkflowException {

		logger.debug("Adding Person to Index");
		try {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(SOLR_USER_UUID_KEY, authValues.getUserUuid().toString());
			doc.addField(SOLR_PERSON_REF_KEY, peopleDetails.getPersonUuid().toString());
			doc.addField(SOLR_FORENAME_KEY, peopleDetails.getForename());
			doc.addField(SOLR_LASTNAME_REG_KEY, peopleDetails.getLastname());
			logger.debug("Adding Document to Solr: " + doc);
			solrUpdateClient.add(doc);
			solrUpdateClient.commit();
			logger.debug("Document added and committed");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@SuppressWarnings("serial")
	@Override
	public List<UUID> findMatchingPeople(AuthValues authValues, String searchString) throws WorkflowException {
		logger.debug("Find person(s) with search String -> " + searchString + " of user id: " + authValues.getUserUuid().toString());
		try {
		if(searchString == null) {
			searchString = "";
		}

		SolrQuery query = getQuery(authValues, searchString ); 
		QueryResponse response = solrClient.query(solrCore, query);
		
		return getUuidsFromResponse(response.getResults()); 
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw new WorkflowException("Error in search stack.") {
			};
		}
	}

	private List<UUID> getUuidsFromResponse(SolrDocumentList results) {
		logger.info("Response from solr: ");
		logger.info(results.toString());
		List<UUID> retList = new ArrayList<UUID>();
		results.forEach((action) -> {
			retList.add(UUID.fromString((String) action.getFieldValue(SOLR_PERSON_REF_KEY)));
		});
		return retList;
	}



	private SolrQuery getQuery(AuthValues authValues, String searchString) {
		// Replace all non alphanumeric characters
		searchString = searchString.replaceAll("[^a-zA-Z0-9]+", "");
		//searchString = searchString.replaceAll(" ", "*OR*");
		SolrQuery query = new SolrQuery();
		StringBuilder queryString = new StringBuilder();
		queryString.append(SOLR_MANDATORY_FIELD_IN_QUERY)
		.append(SOLR_USER_UUID_KEY) 
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append("\"")
		.append(authValues.getUserUuid())
		.append("\"")
		.append(" AND ")
		.append(SOLR_OPEN_MULTIMATCH_SECTION).append(" ")
		.append(SOLR_LASTNAME_REG_KEY)
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append(SOLR_MULTIMATCH_KEY).append(searchString).append(SOLR_MULTIMATCH_KEY).append(" OR ")
		.append(SOLR_FORENAME_KEY)
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append(SOLR_MULTIMATCH_KEY).append(searchString).append(SOLR_MULTIMATCH_KEY).append(" OR ")
		.append(SOLR_PERSON_REF_KEY)
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append(SOLR_MULTIMATCH_KEY).append(searchString).append(SOLR_MULTIMATCH_KEY).append(" ")
		.append(SOLR_CLOSE_MULTIMATCH_SECTION);
		logger.info("Solr query for search string: " + searchString);
		logger.info(queryString.toString());
		return query.setQuery(queryString.toString());

	}

	@Override
	public Health health() {
		Status status = Status.DOWN;
		int statusCode = -1;
		org.springframework.boot.actuate.health.Health.Builder builder =
				new org.springframework.boot.actuate.health.Health.Builder();
		try {
			CoreAdminRequest request = new CoreAdminRequest();
			request.setAction(CoreAdminParams.CoreAdminAction.STATUS);
			CoreAdminResponse response;
			response = request.process(solrClient);
			statusCode = response.getStatus();
			status = statusCode == 0 ? Status.UP : Status.DOWN;

		} catch (SolrServerException | IOException e) {
			// Do nothing as the default status is down
			logger.error(e.getMessage());
		}
		return builder.status(status).withDetail("status", statusCode).build();
	}

}
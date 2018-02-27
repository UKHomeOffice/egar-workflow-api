package uk.gov.digital.ho.egar.workflow.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.BadUpstreamClientWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.DetailBuilder;
import uk.gov.digital.ho.egar.workflow.client.GarSearchClient;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.GarSearchDetails;

@Component
@Profile({"!mock-gar-search"})
public class GarSearchRestClient implements DataClient<GarSearchClient>,GarSearchClient {
	private static Log logger = LogFactory.getLog(GarSearchRestClient.class);

	public GarSearchRestClient(@Autowired DetailBuilder details,
			@Autowired WorkflowPropertiesConfig cfg,
			@Value(SOLR_GAR_CORE_KEY) String core) {
		solrCore = core;
		detailBuilder = details;
		config = cfg;
		try {
			solr  = new HttpSolrClient.Builder().withBaseSolrUrl(config.getSolrApiUrl()).build();
			solrUpdateClient = new HttpSolrClient.Builder().withBaseSolrUrl(config.getSolrApiUrl() + "/" + solrCore).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private DetailBuilder detailBuilder ;

	private WorkflowPropertiesConfig config;

	private SolrClient solr; 
	private SolrClient solrUpdateClient;

	private final String solrCore;

	@Override
	public void addGarToIndex(final AuthValues authValues, final GarSearchDetails garDetails,
			final boolean updateLocation, final boolean updateAircraft) 
					throws WorkflowException {
		try {
			logger.info("Updating a gar in search index for: " + authValues.getUserUuid());
			logger.info("Aircraft update requuested: " + updateAircraft);
			logger.info("Adding Aircraft reg: " + garDetails.getAircraftReg() );
			logger.info("Location update requested: " + updateLocation);
			if (garDetails.getDepartureLocation() != null) {
				try {
					logger.info("Adding location ICAO: " + garDetails.getDepartureLocation().getIcaoCode() );
				} catch (Exception e) {
					// do nothing 
				}
			}
			// Set user uuid
			garDetails.setUserUuid(authValues.getUserUuid());
			// Add and commit gar details to index
			solrUpdateClient.add(getSolrInputDoc(garDetails, updateLocation, updateAircraft));
			solrUpdateClient.commit();
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}

	private SolrInputDocument getSolrInputDoc(GarSearchDetails garDetails, boolean updateLocation, boolean updateAircraft) {

		SolrInputDocument retVal = new SolrInputDocument();
		retVal.addField(SOLR_USER_UUID_KEY, garDetails.getUserUuid().toString());
		retVal.addField(SOLR_GAR_REF_KEY, garDetails.getGarUuid().toString());
		if (updateLocation && garDetails.getDepartureLocation() != null) {
			retVal.addField(SOLR_LOCATION_DEPARTURE_KEY, garDetails.getDepartureLocation().getIcaoCode());
		}
		else {
			retVal.addField(SOLR_LOCATION_DEPARTURE_KEY, "");
		}

		if (updateAircraft) {
			retVal.addField(SOLR_AIRCRAFT_REG_KEY, garDetails.getAircraftReg());
		}

		logger.info("Created a solr input doc for with the requested GAR details");
		try {
			logger.info(retVal.toString());
		}catch (Exception e) {
			// logging error do nothing
		}
		return retVal;
	}

	@Override
	public List<UUID> findMatchingGars(final AuthValues authValues, String searchString) throws BadUpstreamClientWorkflowException  {
		if (searchString == null) {
			searchString = "";
		}
		// Setup a query
		SolrQuery query = buildSolrQuery(authValues,searchString);

		// Get List of response
		QueryResponse response;
		try {
			response = solr.query(solrCore,query);
		} catch (SolrServerException | IOException ex) {
			throw new BadUpstreamClientWorkflowException("Unable to ", ex ) ;
		}

		// Extract all gar uuids from response
		return convertToSearchResults(response.getResults());
	}

	private List<UUID> convertToSearchResults(SolrDocumentList results) {
		List<UUID> garUuids = new ArrayList<UUID>();
		if (results != null && !results.isEmpty()) {
			results.forEach((solrDoc)->{
				try {
					garUuids.add(UUID.fromString((String) solrDoc.getFieldValue(SOLR_GAR_REF_KEY)));
				} catch(Exception e) {
					logger.debug("Error in conversion");
					logger.error(e.getMessage());
				}

			});
		}
		return garUuids;
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
			response = request.process(solr);
			statusCode = response.getStatus();
			status = statusCode == 0 ? Status.UP : Status.DOWN;

		} catch (SolrServerException | IOException e) {
			// Do nothing as the default status is down
			logger.error(e.getMessage());
		}
		return builder.status(status).withDetail("status", statusCode).build();
	}

	@Override
	public void contribute(Builder builder) {
		detailBuilder.withDetail(this,builder);

	}

	private SolrQuery buildSolrQuery(AuthValues authValues, String searchString) {
		// Replace all non alphanumeric characters
		searchString = searchString.replaceAll("[^a-zA-Z0-9]+", "");
		//searchString = searchString.replaceAll(" ", "+");
		SolrQuery query = new SolrQuery();
		StringBuilder queryString = new StringBuilder();
		queryString.append(SOLR_MANDATORY_FIELD_IN_QUERY)
		.append(SOLR_USER_UUID_KEY) 
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append("\"")
		.append(authValues.getUserUuid())
		.append("\"")
		.append(" AND ")
		.append(SOLR_OPEN_MULTIMATCH_SECTION)//.append(" ")
		.append(SOLR_AIRCRAFT_REG_KEY)
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append(SOLR_MULTIMATCH_KEY).append(searchString).append(SOLR_MULTIMATCH_KEY).append(" OR ")
		.append(SOLR_LOCATION_DEPARTURE_KEY)
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append(SOLR_MULTIMATCH_KEY).append(searchString).append(SOLR_MULTIMATCH_KEY).append(" OR ")
		.append(SOLR_GAR_REF_KEY)
		.append(SOLR_QUERY_FIELD_SPACER_KEY)
		.append(SOLR_MULTIMATCH_KEY).append(searchString)//.append(SOLR_MULTIMATCH_KEY).append(" ")
		.append(SOLR_CLOSE_MULTIMATCH_SECTION);
		logger.info("Solr query for search string: "+ searchString);
		logger.info(queryString.toString());
		return query.setQuery(queryString.toString());
	}


}

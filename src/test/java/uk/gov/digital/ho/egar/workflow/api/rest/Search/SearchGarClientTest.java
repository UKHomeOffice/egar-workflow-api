package uk.gov.digital.ho.egar.workflow.api.rest.Search;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.GarSearchClient;
import uk.gov.digital.ho.egar.workflow.model.rest.GarSearchDetails;


/**
 * The tests in this case need to be run sequentially as the 
 * solr index that its being tested against is acting as a store of data
 * Hence the use of the {@link FixMethodOrder} annotation
 * @author Keshava.Grama
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties
		={
				"eureka.client.enabled=false",
				"spring.cloud.config.discovery.enabled=false",
				"test.userest=true",
				"egar.gar.search.core=egar-gar-search",
				"spring.profiles.active="
										+ "mock-gar,"
										+ "mock-location,"
										+ "mock-person,"
										+ "mock-file,"
										+ "mock-submission,"
										+ "mock-aircraft,"
										+ "mock-attribute "
})
@Ignore
@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchGarClientTest {
	static private Log logger = LogFactory.getLog(SearchGarClientTest.class);
	static private UUID thisUser  = UUID.randomUUID();
	@Autowired
	GarSearchClient garSearch;
	
	@Test
	public void aClientNotNull() {
		assertNotNull("Search client not null", garSearch);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void bHealthyConnectionToSolr() {
		DataClient<GarSearchClient> dataInter = (DataClient<GarSearchClient>) garSearch;
		assertNotNull("Search client cast failed", dataInter);
		assertEquals("Status is not zero",Status.UP , dataInter.health().getStatus()); 
	}
	
	@Test
	public void cAddToSolrIndexA() {
		AuthValues authVal = new AuthValues("Abcd", thisUser);
		GarSearchDetails.GarSearchDetailsBuilder builder = GarSearchDetails.builder();
		GarSearchDetails garDetails = builder.aircraftReg("terrewrret").garUuid(UUID.randomUUID()).build();
		
		try {
			garSearch.addGarToIndex(authVal, garDetails, false, true);
		} catch (WorkflowException e) {
			logger.error(e.getMessage());
			fail("Insert threw an exception");
			
		}
	}
	
	@Test
	public void cAddToSolrIndexB() {
		AuthValues authVal = new AuthValues("Abcd", thisUser);
		GarSearchDetails.GarSearchDetailsBuilder builder = GarSearchDetails.builder();
		GarSearchDetails garDetails = builder.aircraftReg("blahblahblah").garUuid(UUID.randomUUID()).build();
		try {
			garSearch.addGarToIndex(authVal, garDetails, false, true);
		} catch (WorkflowException e) {
			logger.error(e.getMessage());
			fail("Insert threw an exception");
			
		}
	}
	
	@Test
	public void cAddToSolrIndexC() {
		AuthValues authVal = new AuthValues("Abcd", thisUser);
		GarSearchDetails.GarSearchDetailsBuilder builder = GarSearchDetails.builder();
		GarSearchDetails garDetails = builder.aircraftReg("gegegegege").garUuid(UUID.randomUUID()).build();
		try {
			garSearch.addGarToIndex(authVal, garDetails, false, true);
		} catch (WorkflowException e) {
			logger.error(e.getMessage());
			fail("Insert threw an exception");
			
		}
	}
	
	@Test
	public void dQuerySolrIndexA() {
		AuthValues authVal = new AuthValues("Abcd", thisUser);
		
		try {
			List<UUID> retVal = garSearch.findMatchingGars(authVal, "lahbl" );
			assertNotNull("Got a null list",retVal);
			assertEquals("Returned more than one", 1, retVal.size());
		} catch (WorkflowException e) {
			logger.error(e.getMessage());
			fail("Insert threw an exception");
			
		}
	}
}

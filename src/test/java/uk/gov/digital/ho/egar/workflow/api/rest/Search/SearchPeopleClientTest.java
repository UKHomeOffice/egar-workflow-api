package uk.gov.digital.ho.egar.workflow.api.rest.Search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.actuate.health.Status;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoRestClients;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.PeopleSearchClient;
import uk.gov.digital.ho.egar.workflow.model.rest.PeopleSearchDetails;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "eureka.client.enabled=false", "spring.cloud.config.discovery.enabled=false",
		"test.userest=true", "egar.people.search.core=egar-people-search",
		"spring.profiles.active=" + "mock-gar," + "mock-location," + "mock-person," + "mock-file," + "mock-submission,"
				+ "mock-aircraft," + "mock-attribute, " + "mock-gar-search" })
@Ignore
@ConditionalIgnore( condition = IgnoreWhenNoRestClients.class )
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchPeopleClientTest {

	@Autowired
	PeopleSearchClient peopleSearchClient;

	private static final Logger logger = LoggerFactory.getLogger(SearchPeopleClientTest.class);
	static private UUID personUser = UUID.randomUUID();

	@Test
	public void aClientShouldNotBeNull() {
		logger.debug("Starting tests");
		assertNotNull("Search person not null", peopleSearchClient);
	}

	@Test
	public void checkTheHealthOfConnectionToSolrServer() {
		@SuppressWarnings("unchecked")
		DataClient<PeopleSearchClient> dClient = (DataClient<PeopleSearchClient>) peopleSearchClient;
		assertNotNull("Search client cast failed", dClient);
		assertEquals("Status is not zero", Status.UP, dClient.health().getStatus());
	}

	@Test
	public void cAddToSolrIndexA() throws SolrServerException, IOException {
		AuthValues authVal = new AuthValues("Abcd", personUser);
		PeopleSearchDetails.PeopleSearchDetailsBuilder builder = PeopleSearchDetails.builder();
		PeopleSearchDetails peopleDetails = builder.forename("Joe").lastname("Bloggs").personUuid(UUID.randomUUID())
				.build();

		try {
			peopleSearchClient.addPersonToIndex(authVal, peopleDetails);
		} catch (WorkflowException e) {
			logger.error(e.getMessage());
			fail("Insert threw an exception");

		}
	}

	@Test
	public void dQuerySolrIndexA() throws SolrServerException, IOException, WorkflowException {
		AuthValues authVal = new AuthValues("Abcd", personUser);
		List<UUID> retVal = peopleSearchClient.findMatchingPeople(authVal, "");
		assertNotNull("Got a null list", retVal);
		assertEquals("Returned more than one", 1, retVal.size());

	}
	
	@Test
	public void eQuerySolrIndexA() throws SolrServerException, IOException, WorkflowException {
		AuthValues authVal = new AuthValues("Abcd", personUser);
		List<UUID> retVal = peopleSearchClient.findMatchingPeople(authVal, "joe bloggs");
		assertNotNull("Got a null list", retVal);
		assertEquals("Returned more than one", 1, retVal.size());

	}
	
	@Test
	public void fQuerySolrIndexA() throws SolrServerException, IOException, WorkflowException {
		AuthValues authVal = new AuthValues("Abcd", personUser);
		List<UUID> retVal = peopleSearchClient.findMatchingPeople(authVal, "12312 12312");
		assertNotNull("Got a null list", retVal);
		assertEquals("Returned more than zero ", 0, retVal.size());

	}
}

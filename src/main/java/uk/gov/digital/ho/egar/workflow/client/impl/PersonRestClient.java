package uk.gov.digital.ho.egar.workflow.client.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.PersonClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientPerson;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.PeopleSearchDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.Person;
import uk.gov.digital.ho.egar.workflow.model.rest.response.PersonWithIdResponse;

import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_BULK;

/**
 * Concrete Implementation of DummyPersonClientImpl
 * 
 * @author Milton.Ezeh
 *
 */

@Component
@Profile({ "!mock-person" })
public class PersonRestClient extends RestClient<PersonClient> implements PersonClient {

	public PersonRestClient(@Autowired final WorkflowPropertiesConfig urlConfig,
			@Autowired final RestTemplate restTemplate) {
		super(urlConfig.getPersonApiURL(), restTemplate);
	}

	@Autowired
	private ConversionService conversionService;


	/**
	 * slf4j logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(GarRestClient.class);

	// FIXME not yet implemented
	@Override
	public PersonWithIdResponse createPerson(final AuthValues authValues, Person person) throws WorkflowException {

		if (logger.isInfoEnabled())
			logger.info("Create a new person.");

		ClientPerson clientPerson = conversionService.convert(person, ClientPerson.class);
		ResponseEntity<ClientPerson> response = doPost(authValues, ROOT_PATH_SEPERATOR, clientPerson,
				ClientPerson.class);

		if (!HttpStatus.OK.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException(response);

		if (logger.isInfoEnabled())
			logger.info("ResponseBody", response.getBody());

		return conversionService.convert(response.getBody(), PersonWithIdResponse.class);
	}

	@Override
	public PersonWithIdResponse updatePerson(final AuthValues authValues, UUID personUuid, Person person)
			throws WorkflowException {

		if (logger.isInfoEnabled())
			logger.info("update a person.");

		String url = ROOT_PATH_SEPERATOR + personUuid + ROOT_PATH_SEPERATOR;
		ClientPerson clientPerson = conversionService.convert(person, ClientPerson.class);
		ResponseEntity<ClientPerson> response = doPost(authValues, url, clientPerson, ClientPerson.class);

		if (!HttpStatus.OK.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException(response);

		if (logger.isInfoEnabled())
			logger.info("ResponseBody", response.getBody());

		return conversionService.convert(response.getBody(), PersonWithIdResponse.class);
	}

	@Override
	public PersonWithIdResponse retrievePerson(final AuthValues authValues, UUID personUuid) throws WorkflowException {

		if (logger.isInfoEnabled())
			logger.info("Request to retrieve a saved person %s for user %s.", personUuid, authValues.getUserUuid());

		String url = ROOT_PATH_SEPERATOR + personUuid + ROOT_PATH_SEPERATOR;

		ResponseEntity<ClientPerson> response = doGet(authValues, url, ClientPerson.class);

		if (!HttpStatus.OK.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException(response);

		if (logger.isInfoEnabled())
			logger.info("ResponseBody", response.getBody());

		return conversionService.convert(response.getBody(), PersonWithIdResponse.class);
	}

	@Override
	public List<PersonWithIdResponse> getBulk(AuthValues authValues, List<UUID> peopleUuids) throws WorkflowException {
		logger.info("Request to retrieve list of people.");

		ResponseEntity<ClientPerson[]> responseArray = doPost(authValues, PATH_BULK, peopleUuids, ClientPerson[].class);

		if (!HttpStatus.OK.equals(responseArray.getStatusCode()))
			throw new UnableToPerformWorkflowException(responseArray);

		List<ClientPerson> responseList = Arrays.asList(responseArray.getBody());

		TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ClientPerson.class));
		TypeDescriptor targetType = TypeDescriptor.collection(List.class,
				TypeDescriptor.valueOf(PersonWithIdResponse.class));

		// Suppressing warning due to using type descriptor within conversion service
		@SuppressWarnings("unchecked")
		List<PersonWithIdResponse> result = (List<PersonWithIdResponse>) conversionService.convert(responseList,
				sourceType, targetType);
		return result;
	}

}

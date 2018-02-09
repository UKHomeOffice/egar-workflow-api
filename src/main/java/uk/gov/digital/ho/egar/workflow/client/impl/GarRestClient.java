package uk.gov.digital.ho.egar.workflow.client.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
import uk.gov.digital.ho.egar.workflow.api.exceptions.GarNotFoundGarClientException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.UnableToPerformWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGar;
import uk.gov.digital.ho.egar.workflow.client.model.ClientGarList;
import uk.gov.digital.ho.egar.workflow.config.WorkflowPropertiesConfig;
import uk.gov.digital.ho.egar.workflow.model.rest.bulk.GarList;
import uk.gov.digital.ho.egar.workflow.model.rest.response.Gar;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;

import static uk.gov.digital.ho.egar.constants.ServicePathConstants.ROOT_PATH_SEPERATOR;
import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.PATH_BULK;

@Component
@Profile({"!mock-gar"})
public class GarRestClient extends RestClient<GarClient> implements GarClient {

	public GarRestClient(@Autowired final WorkflowPropertiesConfig urlConfig, @Autowired final RestTemplate restTemplate) {
		super(urlConfig.getGarApiURL(), restTemplate);
	}

	@Autowired
	private ConversionService conversionService;

	/**
	 * slf4j logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(GarRestClient.class);

	/**
	 * Endpoint to get garAPI.
	 */
	;

	@Override
	public GarSkeleton createGar(final AuthValues authValues) throws WorkflowException {

		if (logger.isInfoEnabled())
			logger.info("Create a new GAR for:" + authValues.getUserUuid());

		ResponseEntity<ClientGar> response = doPost(authValues, ROOT_PATH_SEPERATOR, null, ClientGar.class);

		if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException(response);

		return conversionService.convert(response.getBody(), GarSkeleton.class);
	}

	@Override
	public GarSkeleton updateGar(final AuthValues authValues, UUID garUuid, GarSkeleton gar) throws WorkflowException {

		if (logger.isInfoEnabled())
			logger.info("Update a new GAR for:" + authValues.getUserUuid());

		String url = ROOT_PATH_SEPERATOR + garUuid + ROOT_PATH_SEPERATOR;
		ClientGar clientGarRx = conversionService.convert(gar, ClientGar.class);
		ResponseEntity<ClientGar> response = doPost(authValues, url, clientGarRx, ClientGar.class);

		if (!HttpStatus.OK.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException(response);

		return conversionService.convert(response.getBody(), GarSkeleton.class);
	}

	@Override
	public GarSkeleton getGar(final AuthValues authValues, UUID garUuid) throws WorkflowException {

		if (logger.isInfoEnabled())
			logger.info(String.format("Request to retrieve a saved GAR  %s for user %s", garUuid,
					authValues.getUserUuid()));

		ResponseEntity<ClientGar> response = doGet(authValues, ROOT_PATH_SEPERATOR + garUuid + ROOT_PATH_SEPERATOR,
				ClientGar.class);

		// If Bad Request returns a null GarSkeleton
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			// Not found
			return null;
		}

		return conversionService.convert(response.getBody(), GarSkeleton.class);
	}

	/**
	 * For efficiency call {@link #getGar(AuthValues, UUID)} if the purpose is
	 * to use the GAR straight afterwards.
	 */
	@Override
	public boolean containsGar(final AuthValues authValues, UUID garUuid) throws WorkflowException {
		try {
			Gar gar = getGar(authValues, garUuid);

			if (gar == null)
				return false;

			return gar.getGarUuid().equals(garUuid);

		} catch (GarNotFoundGarClientException e) {
			logger.info("Gar UUID not found");
			return false;
		}
	}

	@Override
	public GarList getListOfGars(final AuthValues authValues) throws WorkflowException {

		logger.info("Request to retrieve list of GARs.");

		ResponseEntity<ClientGarList> response = doGet(authValues, null, ClientGarList.class);

		if (!HttpStatus.OK.equals(response.getStatusCode()))
			throw new UnableToPerformWorkflowException(response);

		logger.info("ResponseBody", response);
		return conversionService.convert(response.getBody(), GarList.class);
	}

	@Override
	public List<GarSkeleton> getBulk(AuthValues authValues, List<UUID> garUuids) throws WorkflowException {
		logger.info("Request to retrieve list of GARs.");

		ResponseEntity<ClientGar[]> responseArray = doPost(authValues, PATH_BULK, garUuids,ClientGar[].class  );
		
		if (!HttpStatus.OK.equals(responseArray.getStatusCode()))
			throw new UnableToPerformWorkflowException(responseArray);
		
		List<ClientGar> responseList = Arrays.asList(responseArray.getBody());

		TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ClientGar.class));
		TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(GarSkeleton.class));

		// Suppressing warning due to using type descriptor within conversion service
		@SuppressWarnings("unchecked")
		List<GarSkeleton> result = (List<GarSkeleton>) conversionService.convert(responseList,sourceType,targetType);
		return result;
	}

}

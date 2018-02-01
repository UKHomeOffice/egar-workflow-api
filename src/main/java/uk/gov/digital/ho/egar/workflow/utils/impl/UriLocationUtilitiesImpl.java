package uk.gov.digital.ho.egar.workflow.utils.impl;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.workflow.utils.UriLocationUtilities;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static uk.gov.digital.ho.egar.workflow.api.WorkflowApi.*;

/**
 * The uri location utilities provide URI's for the provided parameters.
 * Utilises the service name and identifiers found in the workflow api.
 * These can be used to construct redirection responses
 */
@Component
public class UriLocationUtilitiesImpl implements UriLocationUtilities {

	public static class URIFormatException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public URIFormatException(Exception cause) {super(cause);}
	}

	private URI buildUri(String uriPath)  {
			try {
				return new URI(uriPath);
			} catch (Exception e) {
				throw new URIFormatException(e);
			}
		}
		
    /**
     * Gets the attribute URI from the provided gar id
     * @param garUuid The gar uuid.
     * @return The attribute URI
     * @throws URISyntaxException When unable to create a valid URI.
     */
    public URI createAttributeURI(final UUID garUuid) {
        String uriPath = ATTRIBUTE_SERVICE_NAME.replace(PATH_GAR_IDENTIFIER, garUuid + ServicePathConstants.ROOT_PATH_SEPERATOR );
        return buildUri(uriPath);
    }



    /**
     * Gets the person URI from the provided gar id and personid
     * @param garUuid The gar uuid.
     * @param personId  the person uuid
     * @return The person URI
     * @throws URISyntaxException When unable to create a valid URI.
     */
    @Override
    public URI createPersonURI(final UUID garUuid, final UUID personId) {
        String uriPath = PERSON_SERVICE_NAME.replace(PATH_GAR_IDENTIFIER, garUuid + ServicePathConstants.ROOT_PATH_SEPERATOR);
        uriPath = uriPath + personId+ ServicePathConstants.ROOT_PATH_SEPERATOR;
        return buildUri(uriPath);
    }

    /**
     * Gets the gar URI from the provided gar id
     * @param garUuid the gar uuid.
     * @return The Person URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    @Override
    public URI createGarURI(final UUID garUuid) {
        try {
			return new URI(GAR_SERVICE_NAME + garUuid +ServicePathConstants.ROOT_PATH_SEPERATOR);
		} catch (URISyntaxException e) {
		
			throw new URIFormatException(e);
		
		}
    }

    /**
     * Gets the aircraft URI from the provided gar id
     * @param garUuid the gar uuid.
     * @return The Aircraft URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    @Override
    public URI createAircraftUri(final UUID garUuid) {
        try {
			return new URI(AIRCRAFT_SERVICE_NAME.replace(PATH_GAR_IDENTIFIER,  garUuid +ServicePathConstants.ROOT_PATH_SEPERATOR ));
		} catch (URISyntaxException e) {
			throw new URIFormatException(e);
		}
    }

    /**
     * Gets the location URI from the provided gar id and location id
     * @param garUuid the gar uuid.
     * @param locationId the location id
     * @return The Location URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    @Override
    public URI createLocationUri(final UUID garUuid, final UUID locationId) {
        String uriPath = LOCATION_SERVICE_NAME.replace(PATH_GAR_IDENTIFIER,  garUuid +ServicePathConstants.ROOT_PATH_SEPERATOR);
        uriPath = uriPath + locationId;
        return buildUri(uriPath);
    }

    /**
     * Gets the submission URI from the provided gar uuid
     * @param garUuid the gar uuid.
     * @return The Submission URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
	@Override
	public URI createSubmissionURI(final UUID garUuid){
		try {
			return new URI(SUBMISSION_SERVICE_NAME.replace(PATH_GAR_IDENTIFIER,  garUuid +ServicePathConstants.ROOT_PATH_SEPERATOR ));
		} catch (Exception e) {
			throw new URIFormatException(e);
		}
	}

	 /**
     * Gets the file URI from the provided garUuid and fileUuid
     * @param garUuid The gar uuid.
     * @param fileUuid  the file uuid
     * @return The file URI
     * @throws URISyntaxException When unable to create a valid URI.
     */
	@Override
	public URI createFileURI(final UUID garUuid, final UUID fileUuid) {

		String uriPath = FILE_SERVICE_NAME.replace(PATH_GAR_IDENTIFIER, garUuid + ServicePathConstants.ROOT_PATH_SEPERATOR);
        uriPath = uriPath + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR;
        return buildUri(uriPath);
	}
}

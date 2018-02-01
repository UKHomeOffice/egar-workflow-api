package uk.gov.digital.ho.egar.workflow.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * The uri location utilities provide URI's for the provided parameters.
 * These can be used to construct redirection responses
 */
public interface UriLocationUtilities {
    /**
     * Gets an attribute URI for the provided gar id.
     * @param garId The gar uuid.
     * @return The Attribute URI
     * @throws URISyntaxException When unable to construct a valid URI.
     */
    URI createAttributeURI(final UUID garId);

    /**
     * Gets the person URI from the provided gar id and person id
     * @param garId The gar uuid.
     * @param personId  the person uuid
     * @return The person URI
     * @throws URISyntaxException When unable to construct a valid URI.
     */
    URI createPersonURI(final UUID garId, final UUID personId);

    /**
     * Gets the gar URI from the provided gar id
     * @param garId the gar uuid.
     * @return The Person URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    URI createGarURI(final UUID garId);

    /**
     * Gets the aircraft URI from the provided garid
     * @param garId the gar uuid.
     * @return The Aircraft URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    URI createAircraftUri(final UUID garId);

    /**
     * Gets the location URI from the provided garid and location id
     * @param garId the gar uuid.
     * @param locationId the location id
     * @return The Location URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    URI createLocationUri(final UUID garId, final UUID locationId);
    
    /**
     * Gets the submission URI from the provided garUuid
     * @param garUuid the gar uuid.
     * @return The Submission URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
	URI createSubmissionURI(final UUID garUuid) ;

	/**
     * Gets the file URI from the provided garUuid and fileUuid
     * @param garUuid the gar uuid.
     * @return The Submission URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
	URI createFileURI(final UUID garUuid, final UUID fileUuid);

}

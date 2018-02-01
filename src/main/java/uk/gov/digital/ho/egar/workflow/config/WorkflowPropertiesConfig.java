/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 *
 */
@Configuration
@Data
public class WorkflowPropertiesConfig {

	/**
     * Endpoint to get and aircraftAPI.
     */
	@Value("${workflow.egar.aircraft.api.url}")
	private String aircraftApiURL;
	/**
     * Endpoint to get and attributeAPI.
     */
	@Value("${workflow.egar.attribute.api.url}")//FIXME get right value
	private String attributeApiURL;
	/**
     * Endpoint to get and attributeAPI.
     */
	@Value("${workflow.egar.gar.api.url}")//FIXME get right value
	private String garApiURL;	
	
	/**
     * Endpoint to get locationApi.
     */
	@Value("${workflow.egar.location.api.url}")
	private String locationApiURL;
	

	/**
     * Endpoint to get personAPI.
     */
	@Value("${workflow.egar.person.api.url}")
	private String personApiURL;


	/**
     * Endpoint to get submissionAPI.
     */
	@Value("${workflow.egar.submission.api.url}")
	private String submissionApiURL;

	/**
     * Endpoint to get fileAPI.
     */
	@Value("${workflow.egar.file.api.url}")
	private String fileApiURL;

	/**
	 * Endpoint to get fileAPI.
	 */
	@Value("${workflow.egar.fileinfo.api.url}")
	private String fileinfoApiURL;

	/**
	 * The cancellation time before arrival in milliseconds.
	 */
	@Value("${workflow.cancellation.cutoff.time.ms}")
	private long cancellationCutoffTimeMs;

	/**
	 * The maximum number of files allowed
	 */
	@Value("${workflow.max.file.number}")
	private int maxFileNumber;
	
	/**
	 * The maximum total size of files
	 */
	@Value("${workflow.max.total.file.size}")
	private long maxTotalFileSize;
	
	/**
	 * The maximum individual size of file
	 */
	@Value("${workflow.max.file.size}")
	private long maxFileSize;
	
}

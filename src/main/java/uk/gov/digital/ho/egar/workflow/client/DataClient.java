package uk.gov.digital.ho.egar.workflow.client;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;

/**
 * This is a base class which determines the type of operations that a client service will have to perform.
 * @param <T> The interface class this service will 'have'.
 */
public interface DataClient<T> extends HealthIndicator,InfoContributor {
	
}

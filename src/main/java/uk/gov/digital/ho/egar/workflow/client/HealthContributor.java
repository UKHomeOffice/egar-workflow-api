package uk.gov.digital.ho.egar.workflow.client;

import org.springframework.boot.actuate.health.Health;

public interface HealthContributor {

	/**
	 * Return an indication of health.
	 * @return the health for
	 */
	Health health();
	
}

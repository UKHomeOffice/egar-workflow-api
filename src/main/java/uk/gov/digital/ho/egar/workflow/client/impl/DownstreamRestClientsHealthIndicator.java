/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.client.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.workflow.client.HealthContributor;

/**
 * @author localuser
 *
 */
@Component
public class DownstreamRestClientsHealthIndicator implements HealthIndicator {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private final Collection<HealthContributor> healthContributors ;
	
	public DownstreamRestClientsHealthIndicator(Collection<HealthContributor> healthContributors)
	{
		this.healthContributors = healthContributors ;
	}

	@Override
	public Health health() {

		Map<String,Object> overallDetails = new HashMap<String,Object>();
		Status status = Status.UP;
		
		for ( HealthContributor healthContributor : healthContributors )
		{
			String name = healthContributor.getClass().getSimpleName();
			Health health = healthContributor.health();
			
			if (Status.UP.equals(health.getStatus()))
			{
				// No action
				if ( logger.isDebugEnabled()) logger.debug(String.format("Service state %s is UP", name));
			}
			else
			if (Status.OUT_OF_SERVICE.equals(health.getStatus()))
			{
				// Drop the overall service level
				status = Status.OUT_OF_SERVICE ;
				if ( logger.isDebugEnabled()) logger.debug(String.format("Service state %s is OUT_OF_SERVICE", name));
			}
			else
			if (Status.DOWN.equals(health.getStatus()))
			{
				// At this time assume the services are not all vital.
				status = Status.OUT_OF_SERVICE ;
				if ( logger.isDebugEnabled()) logger.debug(String.format("Service state %s is DOWN", name));
			}
			
			// Record the details
			Map<String,Object> details = new HashMap<String,Object>();
			details.put("status", health.getStatus().toString());
			if ( !health.getDetails().isEmpty() )
			{
				details.putAll(health.getDetails());
			}
			overallDetails.put(name, details);
				
		}
		
		return new Health.Builder(status,overallDetails).build();
	}
	
	
}

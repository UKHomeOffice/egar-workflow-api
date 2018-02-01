/**
 * 
 */
package uk.gov.digital.ho.egar.workflow.client.dummy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.info.Info.Builder;

import uk.gov.digital.ho.egar.workflow.client.DataClient;
import uk.gov.digital.ho.egar.workflow.client.DetailBuilder;

/**
 * This class abstracts the A dummy client class and provides the health information.
 * @param <T> The interface class this service will 'have'.
 */
public class DummyClient<T extends DataClient<T>> implements DataClient<T> {

	@Autowired
	private DetailBuilder detailBuilder ;

	
	@Override
	public Health health() {
		return Health.up().build();
	}

	@Override
	public void contribute(Builder builder) {
		detailBuilder.withDetail(this,builder);
		}

}

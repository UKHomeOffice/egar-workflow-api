package uk.co.civica.microservice.util.testing.utils;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.IgnoreCondition;

/**
 * When there is an environment variable <code>test.userest</code> set to any none zero then the test will NOT be ignored.
 * Add to the VM arguments
 * <code>-Dtest.userest=false</code>
 */
public class IgnoreWhenRestClients implements IgnoreCondition {
	public boolean shouldIgnore() {
		return !Boolean.getBoolean("test.userest");
	}
}

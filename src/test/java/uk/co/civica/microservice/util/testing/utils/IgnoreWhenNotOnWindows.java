package uk.co.civica.microservice.util.testing.utils;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.IgnoreCondition;

public class IgnoreWhenNotOnWindows implements IgnoreCondition {
	public boolean shouldIgnore() {
		return !System.getProperty("os.name").startsWith("Windows");
	}
}

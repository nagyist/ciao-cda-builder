package uk.nhs.ciao.cda.builder;

import uk.nhs.ciao.camel.CamelApplication;
import uk.nhs.ciao.camel.CamelApplicationRunner;
import uk.nhs.ciao.configuration.CIAOConfig;
import uk.nhs.ciao.exceptions.CIAOConfigurationException;

/**
 * The main ciao-cda-builder application
 */
public class CDABuilderApplication extends CamelApplication {
	/**
	 * Runs the CDA builder application
	 * 
	 * @see CIAOConfig#CIAOConfig(String[], String, String, java.util.Properties)
	 * @see CamelApplicationRunner
	 */
	public static void main(final String[] args) throws Exception {
		final CamelApplication application = new CDABuilderApplication(args);
		CamelApplicationRunner.runApplication(application);
	}
	
	public CDABuilderApplication(final String... args) throws CIAOConfigurationException {
		super("ciao-cda-builder.properties", args);
	}
	
	public CDABuilderApplication(final CIAOConfig ciaoConfig, final String... args) {
		super(ciaoConfig, args);
	}
}

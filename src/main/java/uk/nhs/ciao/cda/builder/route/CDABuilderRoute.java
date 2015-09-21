package uk.nhs.ciao.cda.builder.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.spi.TransactionErrorHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ciao.camel.BaseRouteBuilder;
import uk.nhs.ciao.configuration.CIAOConfig;
import uk.nhs.ciao.exceptions.CIAOConfigurationException;

/**
 * Creates a Camel route for the specified name / property prefix.
 * <p>
 * Each configurable property is determined by:
 * <ul>
 * <li>Try the specific property: <code>${ROOT_PROPERTY}.${name}.${propertyName}</code></li>
 * <li>If missing fallback to: <code>${ROOT_PROPERTY}.${propertyName}</code></li>
 * </ul>
 */
public class CDABuilderRoute extends BaseRouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(CDABuilderRoute.class);
	
	/**
	 * The root property 
	 */
	public static final String ROOT_PROPERTY = "cdaBuilderRoutes";
	
	private final String name;
	private final String inputQueue;
	private final String processorId;
	private final String outputQueue;
	private String inProgressFolderManagerUri;
	
	/**
	 * Creates a new route builder for the specified name / property prefix
	 * 
	 * @param name The route name / property prefix
	 * @throws CIAOConfigurationException If required properties were missing
	 */
	public CDABuilderRoute(final String name, final CIAOConfig config) throws CIAOConfigurationException {
		this.name = name;
		this.inputQueue = findProperty(config, "inputQueue");
		this.processorId = findProperty(config, "processorId");
		this.outputQueue = findProperty(config, "outputQueue");
	}
	
	public void setInProgressFolderManagerUri(final String inProgressFolderManagerUri) {
		this.inProgressFolderManagerUri = inProgressFolderManagerUri;
	}
	
	/**
	 * Try the specific 'named' property then fall back to the general 'all-routes' property
	 */
	private String findProperty(final CIAOConfig config, final String propertyName) throws CIAOConfigurationException {
		final String specificName = ROOT_PROPERTY + "." + name + "." + propertyName;
		final String genericName = ROOT_PROPERTY + "." + propertyName;
		if (config.getConfigKeys().contains(specificName)) {
			return config.getConfigValue(specificName);
		} else if (config.getConfigKeys().contains(genericName)) {
			
			return config.getConfigValue(genericName);
		} else {
			throw new CIAOConfigurationException("Could not find property " + propertyName +
					" for route " + name);
		}
	}

	/**
	 * Configures / creates a new Camel route corresponding to the set of CIAO-config
	 * properties associated with the route name.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void configure() throws Exception {
		from("jms:queue:" + inputQueue)
		.id("cda-builder-" + name)
		.errorHandler(new TransactionErrorHandlerBuilder()
				.maximumRedeliveries(0)) // redeliveries are disabled (building in only tried once)
		.transacted("PROPAGATION_NOT_SUPPORTED")
		.doTry()
			.log(LoggingLevel.INFO, LOGGER, "Unmarshalled incoming JSON document")
			.beanRef(processorId, "transform")
			.marshal().json(JsonLibrary.Jackson)
			.setHeader(Exchange.FILE_NAME, simple("${file:name.noext}.json"))
			.to("jms:queue:" + outputQueue)
		.doCatch(Exception.class)
			.log(LoggingLevel.ERROR, LOGGER, "Exception while builder CDA document")
			.to("log:" + LOGGER.getName() + "?level=ERROR&showCaughtException=true")
			.handled(false);
	}
}

package uk.nhs.ciao.cda.builder;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;

import uk.nhs.ciao.camel.CamelApplication;
import uk.nhs.ciao.cda.builder.route.CDABuilderRoute;
import uk.nhs.ciao.configuration.CIAOConfig;
import uk.nhs.ciao.docs.parser.route.InProgressFolderManagerRoute;

/**
 * Configures multiple camel CDA builder routes determined by properties specified
 * in the applications registered {@link CIAOConfig}.
 * <p>
 * The 'bootstrap' / {@link #ROOT_PROPERTY} determines which named routes to created (via
 * a comma-separated list).
 * <p>
 * The properties of each route are then looked up via the <code>${ROOT_PROPERTY}.${routeName}.${propertyName}</code>,
 * falling back to <code>${ROOT_PROPERTY}.${propertyName}</code> if a specified property is not provided.
 * This allows for shorthand specification of properties when they are shared across multiple routes.
 * <p>
 * The following properties are supported per named route:
 * <dl>
 * <dt>inputQueue<dt>
 * <dd>The file path (absolute or relative to the working directory) of the input folder to monitor</dd>
 * 
 * <dt>outputQueue<dt>
 * <dd>The name of the queue output messages should be sent to</dd>
 */
public class CDABuilderRoutes implements RoutesBuilder {
	/**
	 * Creates multiple document parser routes
	 * 
	 * @throws Exception If required CIAO-config properties are missing
	 */
	@Override
	public void addRoutesToCamelContext(final CamelContext context) throws Exception {
		addCDABuilderRoute(context);
		
		// Services
		addInProgressFolderManagerRoute(context);
	}
	
	private void addCDABuilderRoute(final CamelContext context) throws Exception {
		final CIAOConfig config = CamelApplication.getConfig(context);
		
		final String[] routeNames = config.getConfigValue(CDABuilderRoute.ROOT_PROPERTY).split(",");
		for (final String routeName: routeNames) {
			final CDABuilderRoute route = new CDABuilderRoute(
					routeName, config);
			route.setInProgressFolderManagerUri("direct:in-progress-folder-manager");
			context.addRoutes(route);
		}
	}
	
	private void addInProgressFolderManagerRoute(final CamelContext context) throws Exception {
		final InProgressFolderManagerRoute route = new InProgressFolderManagerRoute();
		
		route.setInProgressFolderManagerUri("direct:in-progress-folder-manager");
		route.setInternalRoutePrefix("in-progress-folder-manager");
		route.setInProgressFolderRootUri("file:{{inProgressFolder}}");
		
		context.addRoutes(route);
	}
}

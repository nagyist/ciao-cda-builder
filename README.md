# ciao-cda-builder

*CIP to build a CDA document from a simple set of JSON input data*

## Introduction

The purpose of this CIP is to convert an incoming [parsed document](https://github.com/nhs-ciao/ciao-docs-parser/blob/master/docs/parsed-document.md) into one of the [ITK](http://systems.hscic.gov.uk/interop/itk)-specified [CDA](http://www.hl7.org.uk/version3group/cda.asp) document formats.

`ciao-cda-builder` is built on top of [Apache Camel](http://camel.apache.org/) and [Spring Framework](http://projects.spring.io/spring-framework/), and can be run as a stand-alone Java application, or via [Docker](https://www.docker.com/). The [ITK Payloads](https://bitbucket.org/itk/itk-payloads) library is used to build the outgoing CDA documents.

Each application can host multiple [routes](http://camel.apache.org/routes.html), where each route follows the following basic structure:

>   input queue (JMS) -\> [JsonToCDADocumentTransformer](./src/main/java/uk/nhs/ciao/cda/builder/processor/JsonToCDADocumentTransformer.java) -\> output queue (JMS)

-	*The input and output queues both use the JSON-encoded representation of [ParsedDocument](https://github.com/nhs-ciao/ciao-docs-parser/blob/master/docs/parsed-document.md). In the output queue, `ParsedDocument.originalDocument` contains the constructed CDA document.*

The details of the JMS queues and document enrichers are specified at runtime through a combination of [ciao-configuration](https://github.com/nhs-ciao/ciao-utils) properties and Spring XML files.

**Supported CDA types:**

> Incoming documents can specify which type of CDA document to build by include the corresponding interaction id using the `itkHandlingSpec` property on the incoming document.

| Specification | Handler | Interaction ID |
| --------------------- | -------------- | ------- |
| Transfer of Care | [TransferOfCarePayloadHandler](./src/main/java/uk/nhs/ciao/cda/builder/processor/TransferOfCarePayloadHandler.java) | `urn:nhs-itk:interaction:primaryRecipienteDischargeInpatientDischargeSummaryDocument-v1-0` |

Configuration
-------------

For further details of how ciao-configuration and Spring XML interact, please see [ciao-core](https://github.com/nhs-ciao/ciao-core).

### Spring XML

On application start-up, a series of Spring Framework XML files are used to construct the core application objects. The created objects include the main Camel context, input/output components, routes and any intermediate processors.

The configuration is split into multiple XML files, each covering a separate area of the application. These files are selectively included at runtime via CIAO properties, allowing alternative technologies and/or implementations to be chosen. Each imported XML file can support a different set of CIAO properties.

The Spring XML files are loaded from the classpath under the [META-INF/spring](./src/main/resources/META-INF/spring) package.

**Core:**

-   `beans.xml` - The main configuration responsible for initialising properties, importing additional resources and starting Camel.

**Processors:**

-   `processors/default.xml` - Creates a single `JsonToCDADocumentTransformer` to convert incoming documents into CDA format.

**Messaging:**

-   `messaging/activemq.xml` - Configures ActiveMQ as the JMS implementation for input/output queues.
-   `messaging/activemq-embedded.xml` - Configures an internal embedded ActiveMQ as the JMS implementation for input/output queues. *(For use during development/testing)*

### CIAO Properties

At runtime ciao-cda-builder uses the available CIAO properties to determine which Spring XML files to load, which Camel routes to create, and how individual routes and components should be wired.

**Camel Logging:**

-	`camel.log.mdc` - Enables/disables [Mapped Diagnostic Context](http://camel.apache.org/mdc-logging.html) in Camel. If enabled, additional Camel context properties will be made available to Log4J and Logstash. 
-	`camel.log.trace` - Enables/disables the [Tracer](http://camel.apache.org/tracer.html) interceptor for Camel routes.
-	`camel.log.debugStreams` - Enables/disables [debug logging of streaming messages](http://camel.apache.org/how-do-i-enable-streams-when-debug-logging-messages-in-camel.html) in Camel.

**Spring Configuration:**

-   `processorConfig` - Selects which processor configuration to load:
    `processors/${processorConfig}.xml`

-   `messagingConfig` - Selects which messaging configuration to load:
    `messaging/${messagingConfig}.xml`

**Routes:**

-   `cdaBuilderRoutes` - A comma separated list of route names to build

The list of route names serves two purposes. Firstly it determines how many routes to build, and secondly each name is used as a prefix to specify the individual properties of that route.

**Route Configuration:**

>   For 'specific' properties unique to a single route, use the prefix:
>   `cdaBuilderRoutes.${routeName}.`
>
>   For 'generic' properties covering all routes, use the prefix:
>   `cdaBuilderRoutes.`

-   `inputQueue` - Selects which queue to consume incoming documents from
-   `processorId` - The Spring ID of the processor to use when converting documents
-   `outputQueue` - Selects which queue to publish the constructed CDA documents to

**In-progress Folder:**
> Details of the in-progress folder structure are available in the `ciao-docs-finalizer` [state machine](https://github.com/nhs-ciao/ciao-docs-finalizer/blob/master/docs/state-machine.md) documentation.

> `ciao-docs-parser` provides the [InProgressFolderManagerRoute](https://github.com/nhs-ciao/ciao-docs-parser/blob/master/ciao-docs-parser-model/src/main/java/uk/nhs/ciao/docs/parser/route/InProgressFolderManagerRoute.java) class to support storing control and event files in the in-progress directory.

- `inProgressFolder` - Defines the root folder that *document upload process* events are written to.

**Default Processor​:**

>   The default processor configuration does not currently support any additional properties.

### Example
```INI
# Camel logging
camel.log.mdc=true
camel.log.trace=false
camel.log.debugStreams=false

# Select which processor config to use (via dynamic spring imports)
processorConfig=default

# Select which messaging config to use (via dynamic spring imports)
messagingConfig=activemq
#messagingConfig=activemq-embedded

# ActiveMQ settings (if messagingConfig=activemq)
activemq.brokerURL=tcp://localhost:61616
activemq.userName=smx
activemq.password=smx

# Setup route names (and how many routes to build)
cdaBuilderRoutes=default

# Setup 'shared' properties across all-routes
cdaBuilderRoutes.outputQueue=cda-documents
cdaBuilderRoutes.processorId=processor

# Setup per-route properties (can override the shared properties)
cdaBuilderRoutes.default.inputQueue=enriched-documents

inProgressFolder=./in-progress
```

Building and Running
--------------------

To pull down the code, run:

	git clone https://github.com/nhs-ciao/ciao-cda-builder.git
	
You can then compile the module via:

	mvn clean install -P bin-archive

This will compile a number of related modules - the main CIP module is `ciao-cda-builder`, and the full binary archive (with dependencies) can be found at `target\ciao-cda-builder-{version}-bin.zip`. To run the CIP, unpack this zip to a directory of your choosing and follow the instructions in the README.txt.

The CIP requires access to various file system directories and network ports (dependent on the selected configuration):

**etcd**:
 -  Connects to: `localhost:2379`

**ActiveMQ**:
 -  Connects to: `localhost:61616`

**Filesystem**:
 -  If etcd is not available, CIAO properties will be loaded from: `~/.ciao/`
 -	If an incoming document cannot be converted, the CIP will write an event to the folder specified by the `inProgressFolder` property.

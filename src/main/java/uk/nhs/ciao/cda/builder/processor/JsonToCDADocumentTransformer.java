package uk.nhs.ciao.cda.builder.processor;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import uk.nhs.ciao.docs.parser.Document;
import uk.nhs.ciao.docs.parser.ParsedDocument;
import uk.nhs.interoperability.payloads.Payload;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;

/**
 * Transforms an incoming JSON document into a CDA document determined by the ITK interaction (properties.itkHandlingSpec)
 * <p>
 * To register additional interaction->payload conversions use: {@link #setPayloadHandlers(Collection)}.
 * <p>
 * A default/fall-back payload handler can be registered via {@link #setDefaultPayloadHandler(PayloadHandler)}
 */
public class JsonToCDADocumentTransformer {
	private final ObjectMapper objectMapper;
	private final Map<String, PayloadHandler> payloadHandlersByInteration = Maps.newHashMap();
	private PayloadHandler defaultPayloadHandler;
	
	public JsonToCDADocumentTransformer(final ObjectMapper objectMapper) {
		this.objectMapper = Preconditions.checkNotNull(objectMapper);
	}
	
	public void setPayloadHandlers(final Collection<? extends PayloadHandler> payloadHandlers) {
		payloadHandlersByInteration.clear();
		
		for (final PayloadHandler payloadHandler: payloadHandlers) {
			registerPayloadHandler(payloadHandler);
		}
	}
	
	public void setDefaultPayloadHandler(final PayloadHandler defaultPayloadHandler) {
		this.defaultPayloadHandler = defaultPayloadHandler;
	}
	
	public final void registerPayloadHandler(final PayloadHandler payloadHandler) {
		if (payloadHandler != null) {
			payloadHandlersByInteration.put(payloadHandler.getInteraction(), payloadHandler);
		}
	}
	
	/**
	 * Transforms the incoming JSON document into a CDA encoded parsed document
	 */
	public ParsedDocument transform(final String json) throws IOException, MissingMandatoryFieldException {
		// Only parse the JSON once - then traverse the parsed nodes on each pass
		final JsonNode rootNode = objectMapper.readTree(json);

		final String interaction = getInteration(rootNode);
		final PayloadHandler payloadHandler = getPayloadHandler(interaction);
		if (payloadHandler == null) {
			throw new IOException("Cannot create CDA document - no handler is available to create the payload");
		}
		
		final Payload payload = payloadHandler.transformPayload(objectMapper, rootNode.traverse());
		if (payload == null) {
			throw new IOException("Cannot create CDA document - no payload is available");
		}
		
		final ParsedDocument parsedDocument = objectMapper.readValue(rootNode.traverse(), ParsedDocument.class);

		// The original properties and filename from the incoming JSON are maintained in the outgoing document
		final String name = parsedDocument.getOriginalDocument().getName();
		final Map<String, Object> properties = parsedDocument.getProperties();
		
		return asParsedDocument(name, payload, properties);
	}
	
	private String getInteration(final JsonNode rootNode) {
		String interation = null;
		
		final JsonNode propertiesNode = rootNode.get("properties");
		if (propertiesNode != null) {
			final JsonNode interationNode = propertiesNode.get("itkHandlingSpec");
			if (interationNode != null) {
				interation = interationNode.asText();
			}
		}
		
		return Strings.nullToEmpty(interation).trim();
	}

	private PayloadHandler getPayloadHandler(final String interaction) {
		final PayloadHandler payloadHandler = payloadHandlersByInteration.get(interaction);
		return payloadHandler == null ? defaultPayloadHandler : payloadHandler;
	}
	
	/**
	 * Creates a new ParsedDocument using an encoded clinical document as the payload
	 * and the specified properties
	 */
	private ParsedDocument asParsedDocument(final String name, final Payload payload,
			final Map<String, Object> properties) {
		final byte[] bytes = payload.serialise().getBytes();
		final Document document = new Document(name, bytes, "text/xml");
		
		return new ParsedDocument(document, properties);
	}
}

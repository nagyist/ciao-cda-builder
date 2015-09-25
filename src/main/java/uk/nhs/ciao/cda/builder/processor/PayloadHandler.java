package uk.nhs.ciao.cda.builder.processor;

import java.io.IOException;

import uk.nhs.interoperability.payloads.Payload;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles the transformation of an incoming JSON document into a ITK payload
 * for a specific type of ITK interaction
 */
public interface PayloadHandler {
	/**
	 * The interaction handled by the handler
	 */
	String getInteraction();
	
	/**
	 * Transforms the incoming JSON into a payload
	 * 
	 * @param objectMapper The JSON object mapper to use when handling type conversions
	 * @param parser The parser containing the JSON data to transform
	 * @return The transformed payload
	 */
	Payload transformPayload(final ObjectMapper objectMapper, final JsonParser parser) throws IOException, MissingMandatoryFieldException;
}
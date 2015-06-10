package uk.nhs.ciao.cda.builder.json;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures Jackson object mappers to handle conversion to itk-payloads
 */
public class ObjectMapperConfigurator {
	/**
	 * Convenience factory method that creates and configures a new object mapper
	 */
	public ObjectMapper createObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		configure(objectMapper);
		return objectMapper;
	}
	
	/**
	 * Configures the specified object mapper
	 * 
	 * @param objectMapper The object mapper to configure
	 */
	public void configure(final ObjectMapper objectMapper) {
		objectMapper.registerModule(new CDABuilderModule());
		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
		objectMapper.enable(Feature.ALLOW_COMMENTS);
	}
}

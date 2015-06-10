package uk.nhs.ciao.cda.builder.json;

import java.io.IOException;

import uk.nhs.interoperability.payloads.DateValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

/**
 * Deserializer for {@link DateValue} instances.
 * <p>
 * The serialized value is {@link DateValue#toString()}
 * <p>
 * Also consider adding an abstract type mapping for HL7Date -> DateValue to Jackson
 * at startup. With this added, jackson uses this class to handle fields declared
 * as the HL7Date interface (as well as DateValue)
 */
class DateValueDeserializer extends FromStringDeserializer<DateValue> {
	private static final long serialVersionUID = -5953739794572642066L;
	
	/**
	 * Creates a new deserializer instance for {@link DateValue}
	 */
	public DateValueDeserializer() {
		super(DateValue.class);
	}

	@Override
	protected DateValue _deserialize(final String value, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return new DateValue(value);
	}	
}

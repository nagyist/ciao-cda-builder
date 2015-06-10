package uk.nhs.ciao.cda.builder.json;

import java.io.IOException;

import uk.nhs.interoperability.payloads.vocabularies.VocabularyEntry;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Jackson serializer for {@link VocabularyEntry}
 * <p>
 * The serialized value is {@link VocabularyEntry#getCode()}
 */
class VocabularyEntrySerializer extends StdScalarSerializer<VocabularyEntry> {
	private static final long serialVersionUID = 8712166069919187387L;

	public VocabularyEntrySerializer() {
		super(VocabularyEntry.class);
	}
	
	@Override
	public void serialize(final VocabularyEntry value, final JsonGenerator jgen,
			final SerializerProvider provider) throws IOException, JsonGenerationException {
		jgen.writeString(value.getCode());
	}

}

package uk.nhs.ciao.cda.builder;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import uk.nhs.ciao.cda.builder.json.TransferOfCareDocument;
import uk.nhs.ciao.docs.parser.Document;
import uk.nhs.ciao.docs.parser.ParsedDocument;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.toc_edischarge_draftB.ClinicalDocument;

public class JsonToTransferOfCareDocumentTransformer {
	private final ObjectMapper objectMapper;
	
	public JsonToTransferOfCareDocumentTransformer(final ObjectMapper objectMapper) {
		this.objectMapper = Preconditions.checkNotNull(objectMapper);
	}
	
	public ParsedDocument transform(final String json) throws IOException, MissingMandatoryFieldException {
		// Only parse the JSON once - then traverse the parsed nodes on each pass
		final JsonNode rootNode = objectMapper.readTree(json);

		final TransferOfCareDocument transferOfCareDocument = objectMapper.readValue(rootNode.traverse(), TransferOfCareDocument.class);
		final ClinicalDocument clinicalDocument = transferOfCareDocument.createClinicalDocument();
		
		final ParsedDocument parsedDocument = objectMapper.readValue(rootNode.traverse(), ParsedDocument.class);

		// The original properties and filename from the incoming JSON are maintained in the outgoing document
		final String name = parsedDocument.getOriginalDocument().getName();
		final Map<String, Object> properties = parsedDocument.getProperties();
		
		return asParsedDocument(name, clinicalDocument, properties);
	}
	
	/**
	 * Creates a new ParsedDocument using an encoded clinical document as the payload
	 * and the specified properties
	 */
	private ParsedDocument asParsedDocument(final String name, final ClinicalDocument clinicalDocument,
			final Map<String, Object> properties) {
		final byte[] bytes = clinicalDocument.serialise().getBytes();
		final Document document = new Document(name, bytes, "text/xml");
		
		return new ParsedDocument(document, properties);
	}
}

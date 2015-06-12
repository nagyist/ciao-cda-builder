package uk.nhs.ciao.cda.builder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import uk.nhs.ciao.cda.builder.json.TransferOfCareDocument;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;

public class JsonToNonCodedCDADocumentTransformer {
	private final ObjectMapper objectMapper;
	
	public JsonToNonCodedCDADocumentTransformer(final ObjectMapper objectMapper) {
		this.objectMapper = Preconditions.checkNotNull(objectMapper);
	}
	
	public String serialise(final String json) throws JsonProcessingException, IOException, MissingMandatoryFieldException {
		return transform(json).serialise();
	}
	
	public ClinicalDocument transform(final String json) throws JsonProcessingException, IOException, MissingMandatoryFieldException {
		final TransferOfCareDocument document = objectMapper.readValue(json, TransferOfCareDocument.class);
		return document.createClinicalDocument();
	}
}

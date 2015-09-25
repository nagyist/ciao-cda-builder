package uk.nhs.ciao.cda.builder.processor;

import java.io.IOException;

import uk.nhs.ciao.cda.builder.json.TransferOfCareDocument;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.toc_edischarge_draftB.ClinicalDocument;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles the transformation of an incoming JSON document into a Transfer Of Care clinical document
 * payload.
 */
public class TransferOfCarePayloadHandler implements PayloadHandler {
	/**
	 * The ITK interaction associated with the transfer of care documents
	 */
	public static final String INTERACTION = "urn:nhs-itk:interaction:primaryRecipienteDischargeInpatientDischargeSummaryDocument-v1-0";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInteraction() {
		return INTERACTION;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClinicalDocument transformPayload(final ObjectMapper objectMapper,
			final JsonParser parser) throws IOException, MissingMandatoryFieldException {
		final TransferOfCareDocument transferOfCareDocument = objectMapper.readValue(parser, TransferOfCareDocument.class);
		return transferOfCareDocument.createClinicalDocument();
	}
}
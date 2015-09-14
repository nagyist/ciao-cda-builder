package uk.nhs.ciao.cda.builder.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.nhs.ciao.docs.parser.Document;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.helpers.TransferOfCareDraftBDocumentCreationHelper;
import uk.nhs.interoperability.payloads.toc_edischarge_draftB.ClinicalDocument;
import uk.nhs.interoperability.payloads.vocabularies.internal.AttachmentType;

public class TransferOfCareDocument {
	private JsonTransferOfCareFields properties;	
	private Document originalDocument;
	
	@JsonCreator
	public TransferOfCareDocument(@JsonProperty("properties") final JsonTransferOfCareFields properties,
			@JsonProperty("originalDocument") final Document originalDocument) {
		this.properties = properties == null ? new JsonTransferOfCareFields() : properties;
		this.originalDocument = originalDocument;
	}
	
	@JsonProperty("properties")
	public JsonTransferOfCareFields getProperties() {
		return properties;
	}
	
	@JsonProperty
	public Document getOriginalDocument() {
		return originalDocument;
	}
	
	public ClinicalDocument createClinicalDocument() throws MissingMandatoryFieldException {
		if (properties != null) {
			properties.normalise();
		}
		final ClinicalDocument document = TransferOfCareDraftBDocumentCreationHelper.createDocument(properties);
		if (originalDocument != null) {
			TransferOfCareDraftBDocumentCreationHelper.addNonXMLBody(document,
					AttachmentType.Base64, originalDocument.getMediaType(),
					originalDocument.getBase64Content());
		}
		
		return document;
	}
}

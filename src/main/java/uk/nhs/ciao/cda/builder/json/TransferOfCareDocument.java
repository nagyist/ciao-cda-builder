package uk.nhs.ciao.cda.builder.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.nhs.ciao.docs.parser.Document;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;
import uk.nhs.interoperability.payloads.vocabularies.internal.AttachmentType;

public class TransferOfCareDocument {
	private TransferOfCareCommonFields properties;	
	private Document originalDocument;
	
	@JsonCreator
	public TransferOfCareDocument(@JsonProperty("properties") final TransferOfCareCommonFields properties,
			@JsonProperty("originalDocument") final Document originalDocument) {
		this.properties = properties == null ? new TransferOfCareCommonFields() : properties;
		this.originalDocument = originalDocument;
	}
	
	@JsonProperty("properties")
	public TransferOfCareCommonFields getProperties() {
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
		final ClinicalDocument document = TransferOfCareDocumentCreationHelper.createDocument(properties);
		if (originalDocument != null) {
			TransferOfCareDocumentCreationHelper.addNonXMLBody(document,
					AttachmentType.Base64, originalDocument.getMediaType(),
					originalDocument.getBase64Content());
		}
		
		return document;
	}
}

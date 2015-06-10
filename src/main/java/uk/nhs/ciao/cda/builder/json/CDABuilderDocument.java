package uk.nhs.ciao.cda.builder.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.nhs.ciao.docs.parser.Document;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.helpers.NonCodedCDACommonFields;
import uk.nhs.interoperability.payloads.helpers.NonCodedCDADocumentCreationHelper;
import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;
import uk.nhs.interoperability.payloads.vocabularies.internal.AttachmentType;

public class CDABuilderDocument {
	private NonCodedCDACommonFields properties;	
	private Document originalDocument;
	
	@JsonCreator
	public CDABuilderDocument(@JsonProperty("properties") final NonCodedCDACommonFields properties,
			@JsonProperty("originalDocument") final Document originalDocument) {
		this.properties = properties == null ? new NonCodedCDACommonFields() : properties;
		this.originalDocument = originalDocument;
	}
	
	@JsonProperty("properties")
	public NonCodedCDACommonFields getProperties() {
		return properties;
	}
	
	@JsonProperty
	public Document getOriginalDocument() {
		return originalDocument;
	}
	
	public ClinicalDocument createClinicalDocument() throws MissingMandatoryFieldException {
		final ClinicalDocument document = NonCodedCDADocumentCreationHelper.createDocument(properties);
		if (originalDocument != null) {
			NonCodedCDADocumentCreationHelper.addNonXMLBody(document,
					AttachmentType.Base64, originalDocument.getMediaType(),
					originalDocument.getBase64Content());
		}
		
		return document;
	}
}

package uk.nhs.ciao.cda.builder;

import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;
import uk.nhs.interoperability.payloads.vocabularies.internal.AttachmentType;

public class JsonToNonCodedCDADocumentTransformer {
	public String serialise(final ParsedDocument parsedDocument) {
		return transform(parsedDocument).serialise();
	}
	
	public ClinicalDocument transform(final ParsedDocument parsedDocument) {
		final ClinicalDocument template = new ClinicalDocument();
		
		// TODO: Map properties from parsed document
		
		attachOriginalDocument(template, parsedDocument);
		
		return template;
	}
	
	private void attachOriginalDocument(final ClinicalDocument template, final ParsedDocument parsedDocument) {
		final Document originalDocument = parsedDocument.getOriginalDocument();
		if (originalDocument == null || originalDocument.isEmpty()) {
			return;
		}
		
		// Non XML Body
		template.setNonXMLBodyType(AttachmentType.Base64.code);
		template.setNonXMLBodyMediaType(originalDocument.getMediaType());
		template.setNonXMLBodyText(originalDocument.getBase64Content());
	}
}

package uk.nhs.ciao.cda.builder.json;

import org.w3c.dom.Document;

import uk.nhs.ciao.cda.builder.json.JsonMixins.AddressMixin;
import uk.nhs.ciao.cda.builder.json.JsonMixins.DateRangeMixin;
import uk.nhs.ciao.cda.builder.json.JsonMixins.PersonNameMixin;
import uk.nhs.interoperability.payloads.CodedValue;
import uk.nhs.interoperability.payloads.DateValue;
import uk.nhs.interoperability.payloads.HL7Date;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.DateRange;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;
import uk.nhs.interoperability.payloads.vocabularies.VocabularyEntry;
import uk.nhs.interoperability.payloads.vocabularies.generated.DocumentConsentSnCT;
import uk.nhs.interoperability.payloads.vocabularies.generated.Documenttype;
import uk.nhs.interoperability.payloads.vocabularies.generated.JobRoleName;
import uk.nhs.interoperability.payloads.vocabularies.generated.ParticipationType;
import uk.nhs.interoperability.payloads.vocabularies.generated.Sex;
import uk.nhs.interoperability.payloads.vocabularies.internal.HL7ActType;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A jackson module configured to handle conversion of a JSON encoded
 * {@link Document} into a non-coded CDA {@link ClinicalDocument}.
 * <p>
 * An incoming JSON document can be deserialized into {@link TransferOfCareDocument} and
 * from there the clinical document can be obtained.
 * 
 * @see TransferOfCareDocument
 * @see uk.nhs.ciao.docs.parser.Document
 * @see NonCodedCDACommonFields
 */
public class CDABuilderModule extends SimpleModule {
	private static final long serialVersionUID = -240660064734782043L;

	/**
	 * Creates a new Jackson module to handle non coded CDA documents
	 */
	public CDABuilderModule() {
		super("cda-builder");
		
		init();
	}
	
	/**
	 * Initialize the module by adding jackson mixins, serializers, deserializers etc
	 */
	private void init() {
		setMixInAnnotation(Address.class, AddressMixin.class);
		setMixInAnnotation(DateRange.class, DateRangeMixin.class);
		setMixInAnnotation(PersonName.class, PersonNameMixin.class);
		setMixInAnnotation(CodedValue.class, CodedValueMixin.class);
		
		addDeserializer(DateValue.class, new DateValueDeserializer());

		// Vocab deserializers
		addSexDeserializer();
		addVocabularyEntryDeserializer(Documenttype.class, Documenttype.values());
		addVocabularyEntryDeserializer(JobRoleName.class, JobRoleName.values());
		addVocabularyEntryDeserializer(HL7ActType.class, HL7ActType.values());
		addVocabularyEntryDeserializer(DocumentConsentSnCT.class, DocumentConsentSnCT.values());
		addVocabularyEntryDeserializer(ParticipationType.class, ParticipationType.values());
		
		addSerializer(VocabularyEntry.class, new VocabularyEntrySerializer());
		
		addAbstractTypeMapping(HL7Date.class, DateValue.class);
	}

	/**
	 * Adds a deserializer for a {@link VocabularyEntry} sub-type using the standard mappings
	 */
	private <T extends VocabularyEntry> void addVocabularyEntryDeserializer(final Class<T> type, T[] values) {
		addDeserializer(type, new VocabularyEntryDeserializer<T>(type, values));
	}

	/**
	 * Adds a deserializer for {@link Sex} with the standard mappings, plus a few extra convenience mappings
	 * to help translate human readable documents (male, female, m, f, etc)
	 */
	private void addSexDeserializer() {
		final boolean caseSensitive = false;
		final VocabularyEntryDeserializer<Sex> deserializer = new VocabularyEntryDeserializer<Sex>(Sex.class,
				Sex.values(), caseSensitive);
		
		// Alternate codes
		deserializer.addEntry("not-known", Sex._0);
		deserializer.addEntry("not_known", Sex._0);
		deserializer.addEntry("not known", Sex._0);
		deserializer.addEntry("unknown", Sex._0);
		deserializer.addEntry("u", Sex._0);
		deserializer.addEntry("male", Sex._1);
		deserializer.addEntry("m", Sex._1);		
		deserializer.addEntry("female", Sex._2);
		deserializer.addEntry("f", Sex._2);		
		deserializer.addEntry("not-specified", Sex._9);
		deserializer.addEntry("not_specified", Sex._9);
		deserializer.addEntry("not specified", Sex._9);
		
		addDeserializer(Sex.class, deserializer);
	}
}

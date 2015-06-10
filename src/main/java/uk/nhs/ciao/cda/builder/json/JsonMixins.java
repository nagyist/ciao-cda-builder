package uk.nhs.ciao.cda.builder.json;

import java.util.List;

import uk.nhs.interoperability.payloads.CodedValue;
import uk.nhs.interoperability.payloads.HL7Date;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.DateRange;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.helpers.DocumentRecipient;
import uk.nhs.interoperability.payloads.helpers.NonCodedCDACommonFields;
import uk.nhs.interoperability.payloads.helpers.NonCodedCDAParticipant;
import uk.nhs.interoperability.payloads.vocabularies.generated.JobRoleName;
import uk.nhs.interoperability.payloads.vocabularies.generated.ParticipationType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Jackson mixins to convert a JSON parsed document into a non-coded CDA document
 * <p>
 * {@link CDABuilderModule} provides a Jackson module pre-configured with the
 * mixins required to handled non coded CDA documents
 * 
 * @see CDABuilderModule
 * @see NonCodedCDACommonFields
 */
class JsonMixins {
	// disable auto-detect
	@JsonAutoDetect(
			creatorVisibility=Visibility.NONE,
			fieldVisibility=Visibility.NONE,
			getterVisibility=Visibility.NONE,
			isGetterVisibility=Visibility.NONE,
			setterVisibility=Visibility.NONE)
	public interface DisabledAutoDetectMixin {}
	
	/**
	 * Mixin for {@link NonCodedCDACommonFields}
	 * <p>
	 * Primarily flattens nested Java components into a single JSON object (via {@link JsonUnwrapped})
	 */
	@JsonDeserialize(builder=NonCodedCDACommonFieldsBuilder.class)
	interface NonCodedCDACommonFieldsMixin {
		@JsonUnwrapped(prefix="patient")
		@JsonProperty PersonName getPatientName();
		
		@JsonUnwrapped(prefix="patientAddress")
		@JsonProperty Address getPatientAddress();
		
		@JsonUnwrapped(prefix="usualGPAddress")
		@JsonProperty Address getUsualGPAddress();
		
		@JsonUnwrapped(prefix="documentAuthorAddress")
		@JsonProperty Address getDocumentAuthorAddress();
		
		@JsonUnwrapped(prefix="documentAuthor")
		@JsonProperty PersonName getDocumentAuthorName();
		
		@JsonUnwrapped(prefix="dataEnterer")
		@JsonProperty PersonName getDataEntererName();
		
		@JsonUnwrapped(prefix="authenticator")
		@JsonProperty PersonName getAuthenticatorName();
		
		@JsonUnwrapped(prefix="event")
		@JsonProperty CodedValue getEventCode();
		
		@JsonUnwrapped(prefix="eventPerformer")
		@JsonProperty PersonName getEventPerformerName();
		
		@JsonUnwrapped(prefix="encounter")
		@JsonProperty CodedValue getEncounterType();
		
		@JsonUnwrapped(prefix="encounterLocation")
		@JsonProperty CodedValue getEncounterLocationType();
		
		@JsonUnwrapped(prefix="encounterLocationAddress")
		@JsonProperty Address getEncounterLocationAddress();
	}
	
	/**
	 * Acts as both the returned instance of {@link NonCodedCDACommonFields}
	 * and a jackson builder.
	 * <p>
	 * Jackson will call the build() after setting the properties - this gives us a chance to
	 * handle any post-construction setup
	 * <p>
	 * This class primarily exists to support 'flattened short-cut properties' where a single
	 * recipient, copyRecipient, or participant is specified. These types would normally require
	 * an array and structure to specified in the JSON (and are still supported where multiple
	 * instances are required) - however this provides a way to specify a completely flat list
	 * of key/value pairs in JSON.
	 */
	@JsonPOJOBuilder
	static class NonCodedCDACommonFieldsBuilder extends NonCodedCDACommonFields {
		private DocumentRecipient recipient;
		private DocumentRecipient copyRecipient;
		private NonCodedCDAParticipant participant;
		
		@JsonUnwrapped(prefix="recipient")
		@JsonProperty
		public void setRecipient(final DocumentRecipient recipient) {
			this.recipient = recipient;
		}
		
		@JsonUnwrapped(prefix="copyRecipient")
		@JsonProperty
		public void setCopyRecipient(final DocumentRecipient copyRecipient) {
			this.copyRecipient = copyRecipient;
		}
		
		@JsonUnwrapped(prefix="participant")
		@JsonProperty
		public void setParticipant(final NonCodedCDAParticipant participant) {
			this.participant = participant;
		}
		
		public NonCodedCDACommonFields build() {
			// Work around some pre-construction problems with Jackson creating some blank objects (JsonUnwrapped)
			if (!isEmpty(recipient)) {
				addRecipient(recipient);
			}
			
			if (!isEmpty(copyRecipient)) {
				addCopyRecipient(copyRecipient);
			}
			
			if (!isEmpty(participant)) {
				addParticipant(participant);
			}
			
			return this;
		}		
	}
	
	interface DocumentRecipientMixin {
		@JsonUnwrapped(prefix="")
		@JsonProperty PersonName getRecipientName();
		@JsonUnwrapped(prefix="address")
		@JsonProperty Address getRecipientAddress();
		@JsonProperty("telephone") String getRecipientTelephone();
		@JsonProperty("jobRole") JobRoleName getRecipientJobRole();
		@JsonProperty("odsCode") String getRecipientODSCode();
		@JsonProperty("organisationName") String getRecipientOrganisationName();
		@JsonProperty("sdsId") String getRecipientSDSID();
		@JsonProperty("sdsRoleId") String getRecipientSDSRoleID();
	}
	
	interface NonCodedCDAParticipantMixin {
		@JsonUnwrapped(prefix="")
		@JsonProperty PersonName getParticipantName();
		@JsonProperty("sdsId") String getParticipantSDSID();
		@JsonProperty("sdsRoleID") String getParticipantSDSRoleID();
		@JsonUnwrapped(prefix="address")
		@JsonProperty Address getParticipantAddress();
		@JsonProperty("telephone") String getParticipantTelephone();
		@JsonProperty("odsCode") String getParticipantODSCode();
		@JsonProperty("organisationName") String getParticipantOrganisationName();
		@JsonProperty("type") ParticipationType getParticipantType();
	}
		
	interface AddressMixin extends DisabledAutoDetectMixin {
		@JsonProperty("line") List<String> getAddressLine();
		@JsonProperty String getCity();
		@JsonProperty String getPostcode();
		@JsonProperty("key") String getAddressKey();
		@JsonProperty("full") String getFullAddress();
		@JsonProperty("use") String getAddressUse();
		@JsonProperty String getNullFlavour();
		@JsonProperty String getDescription();
		@JsonProperty DateRange getUseablePeriod();
	}	
	
	interface PersonNameMixin extends DisabledAutoDetectMixin {
		@JsonProperty String getTitle();
		@JsonProperty List<String> getGivenName();
		@JsonProperty String getFamilyName();
		@JsonProperty String getFullName();
		@JsonProperty String getNameType();
		@JsonProperty String getNullFlavour();
	}
	
	@JsonDeserialize(builder=DateRangeBuilder.class)
	public static abstract class DateRangeMixin implements DisabledAutoDetectMixin {}
	
	// Supports alternate property names
	@JsonPOJOBuilder
	public static class DateRangeBuilder {
		@JsonProperty HL7Date low;
		@JsonProperty HL7Date high;
		@JsonProperty HL7Date center;
		
		@JsonProperty void setFrom(final HL7Date low) {
			this.low = low;
		}
		
		@JsonProperty void setTo(final HL7Date high) {
			this.high = high;
		}
		
		@JsonProperty void setOn(final HL7Date center) {
			this.center = center;
		}
		
		public DateRange build() {
			final DateRange range = new DateRange();
			if (low != null) {
				range.setLow(low);
			}
			if (high != null) {
				range.setHigh(high);
			}
			if (center != null) {
				range.setCenter(center);
			}
			
			return range;
		}
	}
	
	// Helper methods - could these be moved to a util class in itk-payloads?
	
	private static boolean isEmpty(final DocumentRecipient recipient) {
		if (recipient == null) {
			return true;
		}
		
		return isEmpty(recipient.getRecipientAddress()) &&
				(recipient.getRecipientJobRole() == null) &&
				isEmpty(recipient.getRecipientName()) &&
				isEmpty(recipient.getRecipientODSCode()) &&
				isEmpty(recipient.getRecipientOrganisationName()) &&
				isEmpty(recipient.getRecipientSDSID()) &&
				isEmpty(recipient.getRecipientSDSRoleID()) &&
				isEmpty(recipient.getRecipientTelephone());
	}
	
	private static boolean isEmpty(final NonCodedCDAParticipant participant) {
		if (participant == null) {
			return true;
		}

		return isEmpty(participant.getParticipantAddress()) &&
				isEmpty(participant.getParticipantName()) &&
				isEmpty(participant.getParticipantODSCode()) &&
				isEmpty(participant.getParticipantOrganisationName()) &&
				isEmpty(participant.getParticipantSDSID()) &&
				isEmpty(participant.getParticipantSDSRoleID()) &&
				isEmpty(participant.getParticipantTelephone()) &&
				participant.getParticipantType() == null;
	}
	
	private static boolean isEmpty(final Address address) {
		if (address == null) {
			return true;
		}
		
		return isEmpty(address.getAddressKey()) &&
				isEmpty(address.getAddressLine()) &&
				isEmpty(address.getAddressUse()) &&
				isEmpty(address.getCity()) &&
				isEmpty(address.getDescription()) &&
				isEmpty(address.getFullAddress()) &&
				isEmpty(address.getNullFlavour()) &&
				isEmpty(address.getPostcode()) &&
				isEmpty(address.getUseablePeriod());
	}
	
	private static boolean isEmpty(final PersonName name) {
		if (name == null) {
			return true;
		}
		
		return isEmpty(name.getFamilyName()) &&
				isEmpty(name.getFullName()) &&
				isEmpty(name.getGivenName()) &&
				isEmpty(name.getNameType()) &&
				isEmpty(name.getNullFlavour()) &&
				isEmpty(name.getTitle());
	}
	
	private static boolean isEmpty(final DateRange dateRange) {
		if (dateRange == null) {
			return true;
		}
		
		return isEmpty(dateRange.getHigh()) &&
				isEmpty(dateRange.getLow()) &&
				isEmpty(dateRange.getCenter());
	}
	
	private static boolean isEmpty(final HL7Date date) {
		return date == null || isEmpty(date.asString());
	}
	
	private static boolean isEmpty(final List<String> values) {
		if (values == null || values.isEmpty()) {
			return true;
		}
		
		for (final String value: values) {
			if (!isEmpty(value)) {
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean isEmpty(final String value) {
		return value == null || value.isEmpty();
	}
}

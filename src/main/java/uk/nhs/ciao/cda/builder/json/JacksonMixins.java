package uk.nhs.ciao.cda.builder.json;

import java.util.List;

import uk.nhs.interoperability.payloads.HL7Date;
import uk.nhs.interoperability.payloads.commontypes.DateRange;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Jackson mixins to convert a JSON parsed document into a non-coded CDA document
 * <p>
 * {@link CDABuilderModule} provides a Jackson module pre-configured with the
 * mixins required to handled non coded CDA documents
 * 
 * @see CDABuilderModule
 * @see TransferOfCareCommonFields
 */
class JacksonMixins {
	// disable auto-detect
	@JsonAutoDetect(
			creatorVisibility=Visibility.NONE,
			fieldVisibility=Visibility.NONE,
			getterVisibility=Visibility.NONE,
			isGetterVisibility=Visibility.NONE,
			setterVisibility=Visibility.NONE)
	interface DisabledAutoDetectMixin {
		// Tagging interface to import Jackson annotations - no additional methods required
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
	public static abstract class DateRangeMixin implements DisabledAutoDetectMixin {
		// Tagging class to specify Jackson annotations - no additional methods required
	}
	
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
}

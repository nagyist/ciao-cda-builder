package uk.nhs.ciao.cda.builder.json;

import uk.nhs.ciao.cda.builder.json.JacksonMixins.DisabledAutoDetectMixin;
import uk.nhs.interoperability.payloads.CodedValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson mixin to handle {@link CodedValue} instances.
 * <p>
 * <strong>This mixin cannot be held within the normal {@link JacksonMixins} class due to
 * the JsonCreater annotation. This annotation only works on top-level classes
 * - inner classes or non-static classes are not supported (jackson fails
 * silently)</strong>
 */
// @JsonCreater will only work on top-level classes - cannot be an inner class
abstract class CodedValueMixin implements DisabledAutoDetectMixin {
	@JsonCreator public CodedValueMixin(@JsonProperty("code") String code,
			@JsonProperty("displayName") String displayName) {}
	
	@JsonProperty abstract String getCode();
	@JsonProperty abstract String getDisplayName();
	@JsonProperty("oid") abstract String getOID();
	@JsonProperty("oid") abstract void setOID(String value);
	@JsonProperty abstract String getReference();
	@JsonProperty abstract void setReference(String value);
}
package uk.nhs.ciao.cda.builder.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.util.Emptiable;
import uk.nhs.interoperability.payloads.vocabularies.generated.ParticipationType;
import static uk.nhs.interoperability.payloads.util.Emptiables.*;

public class TransferOfCareParticipant implements Emptiable, Normalisable {
	@JsonUnwrapped
	@JsonProperty
	private PersonName name;
	
	@JsonProperty
	private String sdsId;
	
	@JsonProperty
	private String sdsRoleId;
	
	@JsonProperty
	private Address address;
	
	@JsonProperty
	private String telephone;
	
	@JsonProperty
	private String odsCode;
	
	@JsonProperty
	private String organisationName;
	
	@JsonProperty
	private ParticipationType type;
	
	public PersonName getName() {
		return name;
	}

	public void setName(PersonName name) {
		this.name = name;
	}

	public String getSdsId() {
		return sdsId;
	}

	public void setSdsId(String sdsId) {
		this.sdsId = sdsId;
	}

	public String getSdsRoleId() {
		return sdsRoleId;
	}

	public void setSdsRoleId(String sdsRoleId) {
		this.sdsRoleId = sdsRoleId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getOdsCode() {
		return odsCode;
	}

	public void setOdsCode(String odsCode) {
		this.odsCode = odsCode;
	}

	public String getOrganisationName() {
		return organisationName;
	}

	public void setOrganisationName(String organisationName) {
		this.organisationName = organisationName;
	}

	public ParticipationType getType() {
		return type;
	}

	public void setType(ParticipationType type) {
		this.type = type;
	}

	@Override
	public boolean isEmpty() {
		return isNullOrEmpty(name) &&
				isNullOrEmpty(sdsId) &&
				isNullOrEmpty(sdsRoleId) &&
				isNullOrEmpty(address) &&
				isNullOrEmpty(telephone) &&
				isNullOrEmpty(odsCode) &&
				isNullOrEmpty(organisationName) &&
				type == null;
	}
	
	@Override
	public void normalise() {
		name = emptyToNull(name);
		address = emptyToNull(address);
	}
}

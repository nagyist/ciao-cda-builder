/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package uk.nhs.ciao.cda.builder.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Sets;

import uk.nhs.interoperability.payloads.CodedValue;
import uk.nhs.interoperability.payloads.DateValue;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.util.Emptiable;
import uk.nhs.interoperability.payloads.vocabularies.generated.DocumentConsentSnCT;
import uk.nhs.interoperability.payloads.vocabularies.generated.Documenttype;
import uk.nhs.interoperability.payloads.vocabularies.generated.JobRoleName;
import uk.nhs.interoperability.payloads.vocabularies.generated.Sex;
import uk.nhs.interoperability.payloads.vocabularies.internal.HL7ActType;
import static uk.nhs.interoperability.payloads.util.Emptiables.*;

/**
 * This is a class to help simplify the creation of the non-coded CDA Document, and hide some of
 * the complexity of the underlying document. Once created using this helper, the document
 * can still be fine-tuned using the methods in objects created.
 * 
 * @author Adam Hatherly
 *
 */
public class TransferOfCareCommonFields implements Normalisable {
	private String documentTitle;
	private Documenttype documentType;
	private DateValue documentEffectiveTime;
	private String documentSetID;
	private int documentVersionNumber = 1;

	@JsonUnwrapped(prefix="patient")
	@JsonProperty private PersonName patientName;
	
	private DateValue patientBirthDate;
	private String patientNHSNo;
	private Boolean patientNHSNoIsTraced;
	private Sex patientGender;
	
	@JsonUnwrapped(prefix="patientAddress")
	@JsonProperty private Address patientAddress;
	
	private String patientTelephone;
	private String patientMobile;
	private String usualGPOrgName;
	private String usualGPODSCode;
	private String usualGPTelephone;
	private String usualGPFax;
	
	@JsonUnwrapped(prefix="usualGPAddress")
	@JsonProperty private Address usualGPAddress;
	
	private DateValue timeAuthored;
	
	@JsonUnwrapped(prefix="documentAuthorAddress")
	@JsonProperty private Address documentAuthorAddress;
	
	private JobRoleName documentAuthorRole;
	private String documentAuthorSDSID;
	private String documentAuthorSDSRoleID;
	private String documentAuthorTelephone;
	
	@JsonUnwrapped(prefix="documentAuthor")
	@JsonProperty private PersonName documentAuthorName;
	
	private String documentAuthorOrganisationODSID;
	private String documentAuthorOrganisationName;
	private String dataEntererSDSID;
	private String dataEntererSDSRoleID;
	
	@JsonUnwrapped(prefix="dataEnterer")
	@JsonProperty private PersonName dataEntererName;

	private String custodianODSCode;
	private String custodianOrganisationName;
	private final Set<DocumentRecipient> recipients;
	private final Set<DocumentRecipient> copyRecipients;
	private String authenticatorSDSID;
	private String authenticatorSDSRoleID;
	
	@JsonUnwrapped(prefix="authenticator")
	@JsonProperty private PersonName authenticatorName;
	
	private DateValue authenticatedTime;
	private final Set<TransferOfCareParticipant> participants;
	
	@JsonUnwrapped(prefix="event")
	@JsonProperty private CodedValue eventCode;

	private HL7ActType eventType;
	private DateValue eventEffectiveFromTime;
	private DateValue eventEffectiveToTime;
	private PersonName eventPerformerName;
	private String eventODSCode;
	private String eventOrganisatioName;
	private DocumentConsentSnCT consent;
	private DateValue encounterFromTime;
	private DateValue encounterToTime;
	
	
	@JsonUnwrapped(prefix="encounter")
	@JsonProperty private CodedValue encounterType;
	
	@JsonUnwrapped(prefix="encounterLocation")
	@JsonProperty private CodedValue encounterLocationType;
	private String encounterLocationName;
	
	@JsonUnwrapped(prefix="encounterLocationAddress")
	@JsonProperty private Address encounterLocationAddress;
	
	// Special-case properties to support 'single-entry collections' shortcuts in Jackson
	private DocumentRecipient recipient;
	private DocumentRecipient copyRecipient;
	private TransferOfCareParticipant participant;
	
	public TransferOfCareCommonFields() {
		this.recipients = Sets.newLinkedHashSet();
		this.copyRecipients = Sets.newLinkedHashSet();
		this.participants = Sets.newLinkedHashSet();
	}
	
	public String getDocumentTitle() {
		return documentTitle;
	}
	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}
	public Documenttype getDocumentType() {
		return documentType;
	}
	public void setDocumentType(Documenttype documentType) {
		this.documentType = documentType;
	}
	public DateValue getDocumentEffectiveTime() {
		return documentEffectiveTime;
	}
	public void setDocumentEffectiveTime(DateValue documentEffectiveTime) {
		this.documentEffectiveTime = documentEffectiveTime;
	}
	public int getDocumentVersionNumber() {
		return documentVersionNumber;
	}
	public void setDocumentVersionNumber(int documentVersionNumber) {
		this.documentVersionNumber = documentVersionNumber;
	}
	public PersonName getPatientName() {
		return patientName;
	}
	public void setPatientName(PersonName patientName) {
		this.patientName = patientName;
	}
	public DateValue getPatientBirthDate() {
		return patientBirthDate;
	}
	public void setPatientBirthDate(DateValue patientBirthDate) {
		this.patientBirthDate = patientBirthDate;
	}
	public String getPatientNHSNo() {
		return patientNHSNo;
	}
	public void setPatientNHSNo(String patientNHSNo) {
		this.patientNHSNo = patientNHSNo;
	}
	public Boolean getPatientNHSNoIsTraced() {
		return patientNHSNoIsTraced;
	}
	public void setPatientNHSNoIsTraced(Boolean patientNHSNoIsTraced) {
		this.patientNHSNoIsTraced = patientNHSNoIsTraced;
	}
	public Sex getPatientGender() {
		return patientGender;
	}
	public void setPatientGender(Sex patientGender) {
		this.patientGender = patientGender;
	}
	public Address getPatientAddress() {
		return patientAddress;
	}
	public void setPatientAddress(Address patientAddress) {
		this.patientAddress = patientAddress;
	}
	public String getPatientTelephone() {
		return patientTelephone;
	}
	public void setPatientTelephone(String patientTelephone) {
		this.patientTelephone = patientTelephone;
	}
	public String getPatientMobile() {
		return patientMobile;
	}
	public void setPatientMobile(String patientMobile) {
		this.patientMobile = patientMobile;
	}
	public String getUsualGPOrgName() {
		return usualGPOrgName;
	}
	public void setUsualGPOrgName(String usualGPOrgName) {
		this.usualGPOrgName = usualGPOrgName;
	}
	public String getUsualGPODSCode() {
		return usualGPODSCode;
	}
	public void setUsualGPODSCode(String usualGPODSCode) {
		this.usualGPODSCode = usualGPODSCode;
	}
	public String getUsualGPTelephone() {
		return usualGPTelephone;
	}
	public void setUsualGPTelephone(String usualGPTelephone) {
		this.usualGPTelephone = usualGPTelephone;
	}
	public String getUsualGPFax() {
		return usualGPFax;
	}
	public void setUsualGPFax(String usualGPFax) {
		this.usualGPFax = usualGPFax;
	}
	public Address getUsualGPAddress() {
		return usualGPAddress;
	}
	public void setUsualGPAddress(Address usualGPAddress) {
		this.usualGPAddress = usualGPAddress;
	}
	public DateValue getTimeAuthored() {
		return timeAuthored;
	}
	public void setTimeAuthored(DateValue timeAuthored) {
		this.timeAuthored = timeAuthored;
	}
	public String getDataEntererSDSID() {
		return dataEntererSDSID;
	}
	public void setDataEntererSDSID(String dataEntererSDSID) {
		this.dataEntererSDSID = dataEntererSDSID;
	}
	public String getDataEntererSDSRoleID() {
		return dataEntererSDSRoleID;
	}
	public void setDataEntererSDSRoleID(String dataEntererSDSRoleID) {
		this.dataEntererSDSRoleID = dataEntererSDSRoleID;
	}
	public PersonName getDataEntererName() {
		return dataEntererName;
	}
	public void setDataEntererName(PersonName dataEntererName) {
		this.dataEntererName = dataEntererName;
	}
	public String getCustodianODSCode() {
		return custodianODSCode;
	}
	public void setCustodianODSCode(String custodianODSCode) {
		this.custodianODSCode = custodianODSCode;
	}
	public String getCustodianOrganisationName() {
		return custodianOrganisationName;
	}
	public void setCustodianOrganisationName(String custodianOrganisationName) {
		this.custodianOrganisationName = custodianOrganisationName;
	}
	public Set<DocumentRecipient> getRecipients() {
		return recipients;
	}
	public void setRecipients(List<DocumentRecipient> recipients) {
		this.recipients.clear();
		if (recipients != null) {
			for (final DocumentRecipient recipient: recipients) {
				addRecipient(recipient);
			}
		}
	}
	public void addRecipient(DocumentRecipient recipient) {
		if (recipient != null) {
			this.recipients.add(recipient);
		}
	}
	public Set<DocumentRecipient> getCopyRecipients() {
		return copyRecipients;
	}
	public void setCopyRecipients(List<DocumentRecipient> copyRecipients) {
		this.copyRecipients.clear();
		if (copyRecipients != null) {
			for (final DocumentRecipient copyRecipient: copyRecipients) {
				addCopyRecipient(copyRecipient);
			}
		}
	}
	public void addCopyRecipient(DocumentRecipient copyRecipient) {
		if (copyRecipient != null) {
			this.copyRecipients.add(copyRecipient);
		}
	}
	public String getAuthenticatorSDSID() {
		return authenticatorSDSID;
	}
	public void setAuthenticatorSDSID(String authenticatorSDSID) {
		this.authenticatorSDSID = authenticatorSDSID;
	}
	public String getAuthenticatorSDSRoleID() {
		return authenticatorSDSRoleID;
	}
	public void setAuthenticatorSDSRoleID(String authenticatorSDSRoleID) {
		this.authenticatorSDSRoleID = authenticatorSDSRoleID;
	}
	public PersonName getAuthenticatorName() {
		return authenticatorName;
	}
	public void setAuthenticatorName(PersonName authenticatorName) {
		this.authenticatorName = authenticatorName;
	}
	public DateValue getAuthenticatedTime() {
		return authenticatedTime;
	}
	public void setAuthenticatedTime(DateValue authenticatedTime) {
		this.authenticatedTime = authenticatedTime;
	}
	public Set<TransferOfCareParticipant> getParticipants() {
		return participants;
	}
	public void setParticipants(Set<TransferOfCareParticipant> participants) {
		this.participants.clear();
		if (participants != null) {
			for (final TransferOfCareParticipant participant: participants) {
				addParticipant(participant);
			}
		}
	}
	public void addParticipant(TransferOfCareParticipant participant) {
		if (participant != null) {
			this.participants.add(participant);
		}
	}
	public CodedValue getEventCode() {
		return eventCode;
	}
	public void setEventCode(CodedValue eventCode) {
		this.eventCode = eventCode;
	}
	public DateValue getEventEffectiveFromTime() {
		return eventEffectiveFromTime;
	}
	public void setEventEffectiveFromTime(DateValue eventEffectiveFromTime) {
		this.eventEffectiveFromTime = eventEffectiveFromTime;
	}
	public DateValue getEventEffectiveToTime() {
		return eventEffectiveToTime;
	}
	public void setEventEffectiveToTime(DateValue eventEffectiveToTime) {
		this.eventEffectiveToTime = eventEffectiveToTime;
	}
	public PersonName getEventPerformerName() {
		return eventPerformerName;
	}
	public void setEventPerformerName(PersonName eventPerformerName) {
		this.eventPerformerName = eventPerformerName;
	}
	public String getEventODSCode() {
		return eventODSCode;
	}
	public void setEventODSCode(String eventODSCode) {
		this.eventODSCode = eventODSCode;
	}
	public String getEventOrganisatioName() {
		return eventOrganisatioName;
	}
	public void setEventOrganisatioName(String eventOrganisatioName) {
		this.eventOrganisatioName = eventOrganisatioName;
	}
	public DocumentConsentSnCT getConsent() {
		return consent;
	}
	public void setConsent(DocumentConsentSnCT consent) {
		this.consent = consent;
	}
	public DateValue getEncounterFromTime() {
		return encounterFromTime;
	}
	public void setEncounterFromTime(DateValue encounterFromTime) {
		this.encounterFromTime = encounterFromTime;
	}
	public DateValue getEncounterToTime() {
		return encounterToTime;
	}
	public void setEncounterToTime(DateValue encounterToTime) {
		this.encounterToTime = encounterToTime;
	}
	public CodedValue getEncounterType() {
		return encounterType;
	}
	public void setEncounterType(CodedValue encounterType) {
		this.encounterType = encounterType;
	}
	public CodedValue getEncounterLocationType() {
		return encounterLocationType;
	}
	public void setEncounterLocationType(CodedValue encounterLocationType) {
		this.encounterLocationType = encounterLocationType;
	}
	public String getEncounterLocationName() {
		return encounterLocationName;
	}
	public void setEncounterLocationName(String encounterLocationName) {
		this.encounterLocationName = encounterLocationName;
	}
	public Address getEncounterLocationAddress() {
		return encounterLocationAddress;
	}
	public void setEncounterLocationAddress(Address encounterLocationAddress) {
		this.encounterLocationAddress = encounterLocationAddress;
	}
	public String getDocumentSetID() {
		return documentSetID;
	}
	public void setDocumentSetID(String documentSetID) {
		this.documentSetID = documentSetID;
	}
	public Address getDocumentAuthorAddress() {
		return documentAuthorAddress;
	}
	public void setDocumentAuthorAddress(Address documentAuthorAddress) {
		this.documentAuthorAddress = documentAuthorAddress;
	}
	public JobRoleName getDocumentAuthorRole() {
		return documentAuthorRole;
	}
	public void setDocumentAuthorRole(JobRoleName documentAuthorRole) {
		this.documentAuthorRole = documentAuthorRole;
	}
	public String getDocumentAuthorSDSID() {
		return documentAuthorSDSID;
	}
	public void setDocumentAuthorSDSID(String documentAuthorSDSID) {
		this.documentAuthorSDSID = documentAuthorSDSID;
	}
	public String getDocumentAuthorSDSRoleID() {
		return documentAuthorSDSRoleID;
	}
	public void setDocumentAuthorSDSRoleID(String documentAuthorSDSRoleID) {
		this.documentAuthorSDSRoleID = documentAuthorSDSRoleID;
	}
	public String getDocumentAuthorTelephone() {
		return documentAuthorTelephone;
	}
	public void setDocumentAuthorTelephone(String documentAuthorTelephone) {
		this.documentAuthorTelephone = documentAuthorTelephone;
	}
	public PersonName getDocumentAuthorName() {
		return documentAuthorName;
	}
	public void setDocumentAuthorName(PersonName documentAuthorName) {
		this.documentAuthorName = documentAuthorName;
	}
	public String getDocumentAuthorOrganisationODSID() {
		return documentAuthorOrganisationODSID;
	}
	public void setDocumentAuthorOrganisationODSID(
			String documentAuthorOrganisationODSID) {
		this.documentAuthorOrganisationODSID = documentAuthorOrganisationODSID;
	}
	public String getDocumentAuthorOrganisationName() {
		return documentAuthorOrganisationName;
	}
	public void setDocumentAuthorOrganisationName(
			String documentAuthorOrganisationName) {
		this.documentAuthorOrganisationName = documentAuthorOrganisationName;
	}
	public HL7ActType getEventType() {
		return eventType;
	}
	public void setEventType(HL7ActType eventType) {
		this.eventType = eventType;
	}
	
	
	// 'single-entry' collection support
	
	/**
	 * Shortcut for single entry - this entry is also added to
	 * the main collection
	 */
	@JsonUnwrapped(prefix="recipient")
	@JsonProperty
	public void setRecipient(final DocumentRecipient recipient) {
		// clear-old entry
		if (this.recipient != null) {
			recipients.remove(this.recipient);
		}
		this.recipient = recipient;
		
		addRecipient(recipient);
	}
	
	/**
	 * Shortcut for single entry - this entry is also added to
	 * the main collection
	 */
	@JsonUnwrapped(prefix="copyRecipient")
	@JsonProperty
	public void setCopyRecipient(final DocumentRecipient copyRecipient) {
		// clear-old entry
		if (this.copyRecipient != null) {
			copyRecipients.remove(this.copyRecipient);
		}
		this.copyRecipient = copyRecipient;
		
		addCopyRecipient(copyRecipient);
	}
	
	/**
	 * Shortcut for single entry - this entry is also added to
	 * the main collection
	 */
	@JsonUnwrapped(prefix="participant")
	@JsonProperty
	public void setParticipant(final TransferOfCareParticipant participant) {
		// clear-old entry
		if (this.participant != null) {
			participants.remove(this.participant);
		}
		this.participant = participant;
		addParticipant(participant);
	}	
	
	/**
	 * Normalises the fields by removing non-null but empty components
	 * 
	 * @see Emptiable
	 */
	@Override
	public void normalise() {
		documentEffectiveTime = emptyToNull(documentEffectiveTime);
		patientName = emptyToNull(patientName);
		patientBirthDate = emptyToNull(patientBirthDate);
		patientAddress = emptyToNull(patientAddress);
		usualGPAddress = emptyToNull(usualGPAddress);
		timeAuthored = emptyToNull(timeAuthored);
		documentAuthorAddress = emptyToNull(documentAuthorAddress);
		documentAuthorName = emptyToNull(documentAuthorName);
		dataEntererName = emptyToNull(dataEntererName);
		authenticatorName = emptyToNull(authenticatorName);
		authenticatedTime = emptyToNull(authenticatedTime);
		
		eventCode = emptyToNull(eventCode);
		eventEffectiveFromTime = emptyToNull(eventEffectiveFromTime);
		eventEffectiveToTime = emptyToNull(eventEffectiveToTime);
		eventPerformerName = emptyToNull(eventPerformerName);
		encounterFromTime = emptyToNull(encounterFromTime);
		encounterToTime = emptyToNull(encounterToTime);
		encounterType = emptyToNull(encounterType);
		encounterLocationType = emptyToNull(encounterLocationType);
		encounterLocationAddress = emptyToNull(encounterLocationAddress);
		
		normalize(recipients);
		normalize(copyRecipients);
		normalize(participants);
		
		// Remove 'single-entry' shortcuts - these are already handled in the main collections
		recipient = null;
		copyRecipient = null;
		participant = null;
	}
	
	private static <T extends Normalisable & Emptiable> void normalize(final Collection<T> values) {
		for (final Iterator<T> iterator = values.iterator(); iterator.hasNext();) {
			final T recipient = iterator.next();
			if (recipient != null) {
				recipient.normalise();
			}
			
			if (isNullOrEmpty(recipient)) {
				iterator.remove();
			}
		}
	}
}

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

import java.util.Date;

import uk.nhs.interoperability.payloads.DateValue;
import uk.nhs.interoperability.payloads.noncodedcdav2.*;
import uk.nhs.interoperability.payloads.templates.*;
import uk.nhs.interoperability.payloads.commontypes.*;
import uk.nhs.interoperability.payloads.exceptions.MissingMandatoryFieldException;
import uk.nhs.interoperability.payloads.util.CDAUUID;
import uk.nhs.interoperability.payloads.vocabularies.generated.*;
import uk.nhs.interoperability.payloads.vocabularies.internal.*;

/**
 * This helper class takes a set of fields in the form of a NonCodedCDACommonFields object, and uses them to
 * create the various Java objects that represent the document. The helper makes a number of assumptions in
 * order to simplify the process of creating a Non-Coded CDA document. For example it assumes that all
 * patients will be identified using an NHS number, and that all staff will be identified using an SDS ID. These
 * assumptions may not fit the specific needs of teams implementing this library in their solution. Developers
 * are encouraged to use this class as a starting point to build on/tweak as required 
 * @author Adam Hatherly
 */
final class TransferOfCareDocumentCreationHelper {
	private TransferOfCareDocumentCreationHelper() {
		// Suppress default constructor
	}
	
	public static ClinicalDocument addNonXMLBody(ClinicalDocument document, AttachmentType encoding, String mimeType, String body) {
		document.setNonXMLBodyMediaType(mimeType);
		document.setNonXMLBodyType(encoding.code);
		document.setNonXMLBodyText(body);
		return document;
	}
	
	public static ClinicalDocument createDocument(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		
		ClinicalDocument template = new ClinicalDocument();
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		DateValue currentDateTime = new DateValue(new Date(), DatePrecision.Minutes);
		
		// ==== We will assume some things and set them accordingly ====
		template.setDocumentId(CDAUUID.generateUUIDString());
		template.setConfidentialityCode(x_BasicConfidentialityKind._N);
		
		// Title
		if (commonFields.getDocumentTitle() != null) {
			template.setDocumentTitle(commonFields.getDocumentTitle());
		} else {
			missingFields.addMissingField("documentTitle", "The document title must be provided");
		}
		
		// Document Type
		if (commonFields.getDocumentType() != null) {
			template.setDocumentType(commonFields.getDocumentType());
		} else {
			missingFields.addMissingField("documentType", "The document type must be provided");
		}
		
		// If no record effective date/time specified, assume the current date/time
		if (commonFields.getDocumentEffectiveTime() == null) {
			template.setEffectiveTime(currentDateTime);
		} else {
			template.setEffectiveTime(commonFields.getDocumentEffectiveTime());
		}
		
		// If no document set ID provided, generate a new one
		if (commonFields.getDocumentSetID() != null) {
			template.setDocumentSetId(commonFields.getDocumentSetID());
		} else {
			template.setDocumentSetId(CDAUUID.generateUUIDString());
		}
		
		// Version defaults to 1 unless set to a different integer value
		template.setDocumentVersionNumber(String.valueOf(commonFields.getDocumentVersionNumber()));
		
		// Patient
		try {
			PatientUniversal patient = createPatient(commonFields);
			template.setPatient(patient);
		} catch (MissingMandatoryFieldException e) {
			missingFields.addMissingFields(e);
		}
		
		// Author
		if (commonFields.getTimeAuthored() == null) {
			template.setTimeAuthored(currentDateTime);
		} else {
			template.setTimeAuthored(commonFields.getTimeAuthored());
		}
		
		try {
			AuthorPersonUniversal author = createAuthor(commonFields);
			template.setAuthor(author);
		} catch (MissingMandatoryFieldException e) {
			missingFields.addMissingFields(e);
		}
		
		// Data Enterer (optional)
		if (commonFields.getDataEntererName() != null) {
			try {
				template.setDataEnterer(createDataEnterer(commonFields));
			} catch (MissingMandatoryFieldException e) {
				missingFields.addMissingFields(e);
			}
		}
		
		// Custodian
		try {
			template.setCustodianOrganisation(createCustodian(commonFields));
		} catch (MissingMandatoryFieldException e) {
			missingFields.addMissingFields(e);
		}
		
		// Recipients
		
		// Having at least one recipient is mandatory
		if (commonFields.getRecipients().isEmpty()) {
			missingFields.addMissingField("recipients", "At least one recipient must be provided");
		} else {
			// Primary Recipients
			for (DocumentRecipient recipient : commonFields.getRecipients()) {
				try {
					Recipient r = createRecipient(recipient);
					template.addPrimaryRecipients(
								new PrimaryRecipient().setRecipient(r));
				} catch (MissingMandatoryFieldException e) {
					missingFields.addMissingFields(e);
				}
			}
			// Copy Recipients
			for (DocumentRecipient recipient : commonFields.getCopyRecipients()) {
				try {
					Recipient r = createRecipient(recipient);
					template.addInformationOnlyRecipients(
								new InformationOnlyRecipient().setRecipient(r));
				} catch (MissingMandatoryFieldException e) {
					missingFields.addMissingFields(e);
				}
			}
		}
		
		// Authenticator
		if (commonFields.getAuthenticatorName() != null) {
			if (commonFields.getAuthenticatedTime() == null) {
				missingFields.addMissingField("authenticatedTime", "The time the document was authenticated must be provided");
			} else {
				template.setTimeAuthenticated(commonFields.getAuthenticatedTime());
			}
			try {
				template.setAuthenticator(createAuthenticator(commonFields));
			} catch (MissingMandatoryFieldException e) {
				missingFields.addMissingFields(e);
			}
		}
		
		// Participants
		for (TransferOfCareParticipant participant : commonFields.getParticipants()) {
			if (participant.getType() == null) {
				missingFields.addMissingField("participantType", "The participant type must be provided");
			}
			try {
				Participant p = createParticipant(participant);
				template.addParticipant((
							new DocumentParticipant()
										.setParticipant(p)
										.setParticipantTypeCode(participant.getType().code)));
			} catch (MissingMandatoryFieldException e) {
				missingFields.addMissingFields(e);
			}
		}
		
		// DocumentationOf
		if (commonFields.getEventCode() != null) {
			try {
				template.addDocumentationOf(new DocumentationOf().setServiceEvent(createServiceEvent(commonFields)));
			} catch (MissingMandatoryFieldException e) {
				missingFields.addMissingFields(e);
			}
		}
		
		// Consent
		if (commonFields.getConsent() != null) {
			template.setAuthorizingConsent(new Consent()
													.setConsentCode(commonFields.getConsent())
													.addID(new ConsentID(CDAUUID.generateUUIDString())));
		}
		
		// Encompassing Encounter
		if (commonFields.getEncounterType() != null) {
			template.setEncompassingEncounter(createEncompassingEncounter(commonFields));
		}

		// We have done all the checks on mandatory fields, so if there are any
		// errors, throw them up to the caller
		if (missingFields.hasEntries()) {
			throw missingFields;
		}		
		
		return template; 
	}
	
	public static PatientUniversal createPatient(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		
		// Null checks for mandatory fields
		if (commonFields.getPatientNHSNoIsTraced() == null) {
			missingFields.addMissingField("PatientNHSNoIsTraced", "The tracing status for the NHS number must be provided");
		}
		if (commonFields.getPatientNHSNo() == null) {
			missingFields.addMissingField("PatientNHSNo", "The patient's NHS number must be provided");
		}
		if (commonFields.getPatientAddress() == null) {
			missingFields.addMissingField("PatientAddress", "The patient's address must be provided");
		}
		if (commonFields.getPatientName() == null) {
			missingFields.addMissingField("PatientName", "The patient's name must be provided");
		}
		if (commonFields.getPatientGender() == null) {
			missingFields.addMissingField("PatientGender", "The patient's gender must be provided");
		}
		if (commonFields.getPatientBirthDate() == null) {
			missingFields.addMissingField("PatientBirthDate", "The patient's date of birth must be provided");
		}
		if (commonFields.getUsualGPODSCode() == null) {
			missingFields.addMissingField("UsualGPODSCode", "The usual GP's ODS Code must be provided");
		}
		if (commonFields.getUsualGPOrgName() == null) {
			missingFields.addMissingField("UsualGPOrgName", "The usual GP's organisation name must be provided");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		PatientUniversal template = new PatientUniversal();
		// NHS Number and trace status
		if (commonFields.getPatientNHSNoIsTraced().booleanValue()) {
			template.addPatientID(new PatientID().setPatientID(commonFields.getPatientNHSNo())
										.setPatientIDType(PatientIDType.VerifiedNHSNumber.code));
		} else {
			template.addPatientID(new PatientID().setPatientID(commonFields.getPatientNHSNo())
					.setPatientIDType(PatientIDType.UnverifiedNHSNumber.code));
		}
		
		// Address
		template.addAddress(commonFields.getPatientAddress());
		
		// Telephone
		if (commonFields.getPatientTelephone() != null) {
			template.addTelephoneNumber(new Telecom()
									.setTelecom("tel:" + commonFields.getPatientTelephone()));
		}
		
		// Mobile
		if (commonFields.getPatientMobile() != null) {
			template.addTelephoneNumber(new Telecom()
									.setTelecom("tel:" + commonFields.getPatientMobile())
									.setTelecomType(TelecomUseType.MobileContact.code));
		}
		
		// Name
		template.addPatientName(commonFields.getPatientName());

		// Gender (actually sex)
		if (commonFields.getPatientGender() != null) {
			template.setSex(commonFields.getPatientGender());
		}

		// Date of birth
		template.setBirthTime(commonFields.getPatientBirthDate());
		
		// Usual GP ODS Code:
		template.setRegisteredGPOrgId(new OrgID()
									.setID(commonFields.getUsualGPODSCode())
									.setType(OrgIDType.ODSOrgID.code));
		
		// Usual GP Org Name
		template.setRegisteredGPOrgName(commonFields.getUsualGPOrgName());
		
		// Usual GP Telephone
		if (commonFields.getUsualGPTelephone() != null) {
			template.addRegisteredGPTelephone(new Telecom()
									.setTelecom("tel:" + commonFields.getUsualGPTelephone())
									.setTelecomType(TelecomUseType.WorkPlace.code));
		}
		
		// Usual GP Fax
		if (commonFields.getUsualGPFax() != null) {
			template.addRegisteredGPTelephone(new Telecom()
									.setTelecom("fax:" + commonFields.getUsualGPFax())
									.setTelecomType(TelecomUseType.WorkPlace.code));
		}
		
		// Usual GP Address
		if (commonFields.getUsualGPAddress() != null) {
			Address add = commonFields.getUsualGPAddress();
			add.setAddressUse(AddressType.WorkPlace.code);
			template.setRegisteredGPAddress(add);
		}
		
		return template;
	}
	
	public static AuthorPersonUniversal createAuthor(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		
		// Null checks for mandatory fields
		if (commonFields.getDocumentAuthorSDSID() == null) {
			missingFields.addMissingField("DocumentAuthorSDSID", "The SDS ID of the document author must be provided");
		}
		if (commonFields.getDocumentAuthorRole() == null) {
			missingFields.addMissingField("DocumentAuthorRole", "The job role of the document author must be provided");
		}
		if (commonFields.getDocumentAuthorName() == null) {
			missingFields.addMissingField("DocumentAuthorName", "The name of the document author must be provided");
		}
		if (commonFields.getDocumentAuthorOrganisationODSID() == null) {
			missingFields.addMissingField("DocumentAuthorOrganisationODSID", "The ID of the organisation the document author belongs to must be provided");
		}
		if (commonFields.getDocumentAuthorOrganisationName() == null) {
			missingFields.addMissingField("DocumentAuthorOrganisationName", "The name of the organisation the document author belongs to must be provided");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		AuthorPersonUniversal template = new AuthorPersonUniversal();
 		
		// Author SID ID
		template.addId(new PersonID()
 						.setType(PersonIDType.SDSID.code)
 						.setID(commonFields.getDocumentAuthorSDSID()));
		
		// Author SID Role ID
		if (commonFields.getDocumentAuthorSDSRoleID() != null) {
			template.addId(new PersonID()
	 						.setType(PersonIDType.SDSRoleProfile.code)
	 						.setID(commonFields.getDocumentAuthorSDSRoleID()));
		}
		
		// Author Job Role
 		template.setJobRoleName(commonFields.getDocumentAuthorRole());
 		
 		// Author Address
 		if (commonFields.getDocumentAuthorAddress() != null) {
 			Address add = commonFields.getDocumentAuthorAddress();
 			add.setAddressUse(AddressType.WorkPlace.code);
 			template.addAddress(add);
 		}
 		
 		// Author telephone number
 		if (commonFields.getDocumentAuthorTelephone() != null) {
 			template.addTelephoneNumber(new Telecom("tel:" + commonFields.getDocumentAuthorTelephone()));
 		}
 		
 		// Author Name
 		template.setName(commonFields.getDocumentAuthorName());
 		
 		// Author ORG ID
		template.setOrganisationId(new OrgID()
									.setID(commonFields.getDocumentAuthorOrganisationODSID())
									.setType(OrgIDType.ODSOrgID.code));
		
		// Author ORG Name
		template.setOrganisationName(commonFields.getDocumentAuthorOrganisationName());
		
		return template;
	}
	
	public static PersonUniversal createDataEnterer(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		// Null checks for mandatory fields
		if (commonFields.getDataEntererSDSID() == null) {
			missingFields.addMissingField("dataEntererSDSID", "The SDS ID of the data enterer must be provided");
		}
		if (commonFields.getDataEntererSDSRoleID() == null) {
			missingFields.addMissingField("dataEntererSDSRoleID", "The SDS Role ID of the data enterer must be provided");
		}
		if (commonFields.getDataEntererName() == null) {
			missingFields.addMissingField("dataEntererName", "The name of the data enterer must be provided");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		PersonUniversal template = new PersonUniversal();
		// SDS ID
		template.addPersonId(new PersonID()
									.setType(PersonIDType.SDSID.code)
									.setOID(commonFields.getDataEntererSDSID()));
		// SDS Role ID
		template.addPersonId(new PersonID()
									.setType(PersonIDType.SDSRoleProfile.code)
									.setOID(commonFields.getDataEntererSDSRoleID()));
		// Name
		template.setPersonName(commonFields.getDataEntererName());
		return template;
	}
	
	public static CustodianOrganizationUniversal createCustodian(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		// Null checks for mandatory fields
		if (commonFields.getCustodianODSCode() == null) {
			missingFields.addMissingField("custodianODSCode", "The ODS ID of the custodian organisation must be provided");
		}
		if (commonFields.getCustodianOrganisationName() == null) {
			missingFields.addMissingField("custodianOrganisationName", "The name of the custodian organisation must be provided");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		CustodianOrganizationUniversal template = new CustodianOrganizationUniversal();
		
		// EPaCCS Hosting Org ID
		template.setId(new OrgID(OrgIDType.ODSOrgID.code, commonFields.getCustodianODSCode()));
		
		// EPaCCS Hosting Org Name
		template.setName(commonFields.getCustodianOrganisationName());
		
		return template;
	}
	
	public static Recipient createRecipient(DocumentRecipient recipient) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		// Null checks for mandatory fields
		if (recipient.getName() == null) {
			missingFields.addMissingField("recipientName", "The name of the recipient must be provided");
		}
		if (recipient.getOdsCode() == null) {
			missingFields.addMissingField("recipientODSCode", "The ODS Code for the organisation of the recipient must be provided");
		}
		if (recipient.getOrganisationName() == null) {
			missingFields.addMissingField("recipientOrganisationName", "The organisation name for the recipient must be provided");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		RecipientPersonUniversal template = new RecipientPersonUniversal();
		
		// ID (NULL)
		template.addId(new RoleID().setNullFlavour(NullFlavour.NA.code));
		
		// recipientName
		template.setName(recipient.getName());
		
		// recipientAddress
		if (recipient.getAddress() != null) {
			template.setAddress(recipient.getAddress());
		}
		
		// recipientTelephone
 		if (recipient.getTelephone() != null) {
 			template.addTelephoneNumber(new Telecom("tel:" + recipient.getTelephone()));
 		}
		
		// recipientJobRole
 		if (recipient.getJobRole() != null) {
 			template.setJobRoleName(recipient.getJobRole());
 		}
 		
		// recipientODSCode
 		template.setOrgId(new OrgID()
 								.setID(recipient.getOdsCode())
 								.setType(OrgIDType.ODSOrgID.code));
 		
		// recipientOrganisationName
 		template.setOrgName(recipient.getOrganisationName());
		
		return template;
	}
	
	public static Participant createParticipant(TransferOfCareParticipant participant) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		// No mandatory fields in this object
		
		DocumentParticipantUniversal template = new DocumentParticipantUniversal();
		
		// participantName
		if (participant.getName() != null) { 
			template.setName(participant.getName());
		}
		// participantSDSID
		if (participant.getSdsId() != null) {
			template.addId(new PersonID()
									.setType(PersonIDType.SDSID.code)
									.setOID(participant.getSdsId()));
			if (participant.getSdsRoleId() == null) {
				missingFields.addMissingField("participantSDSRoleID", "If a participant SDS ID is provided, then an SDS Role ID must also be provided");
				throw missingFields;
			}
			// participantSDSRoleID
			template.addId(new PersonID()
									.setType(PersonIDType.SDSRoleProfile.code)
									.setOID(participant.getSdsRoleId()));
		}
		// participantAddress
		if (participant.getAddress() != null) {
			template.setAddress(participant.getAddress());
		}
		// participantTelephone
 		if (participant.getTelephone() != null) {
 			template.addTelephoneNumber(new Telecom("tel:" + participant.getTelephone()));
 		}
 		// participantODSCode
 		if (participant.getOdsCode() != null) {
	 		template.setOrgId(new OrgID()
									.setID(participant.getOdsCode())
									.setType(OrgIDType.ODSOrgID.code));
 		}
 		// participantOrganisationName
 		if (participant.getOrganisationName() != null) {
 			template.setOrgName(participant.getOrganisationName());
 		}
		return template;
	}
	
	public static PersonUniversal createAuthenticator(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		// Null checks for mandatory fields
		if (commonFields.getAuthenticatorName() == null) {
			missingFields.addMissingField("authenticatorName", "The name of the authenticator must be provided");
		}
		if (commonFields.getAuthenticatorSDSID() == null) {
			missingFields.addMissingField("authenticatorSDSID", "The SDS ID for the authenticator must be provided");
		}
		if (commonFields.getAuthenticatorSDSRoleID() == null) {
			missingFields.addMissingField("authenticatorSDSRoleID", "The SDS Role ID for the authenticator must be provided");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		PersonUniversal template = new PersonUniversal();
		
		// authenticatorSDSID
		template.addPersonId(new PersonID()
									.setID(commonFields.getAuthenticatorSDSID())
									.setType(PersonIDType.SDSID.code));
		
		// authenticatorSDSRoleID
		template.addPersonId(new PersonID()
									.setID(commonFields.getAuthenticatorSDSRoleID())
									.setType(PersonIDType.SDSRoleProfile.code));
		
		// authenticatorName
		template.setPersonName(commonFields.getAuthenticatorName());
		
		return template;
	}
	
	public static ServiceEvent createServiceEvent(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		// Null checks for mandatory fields
		boolean from = commonFields.getEventEffectiveFromTime()!=null;
		boolean to = commonFields.getEventEffectiveToTime()!=null;
		boolean name = commonFields.getEventPerformerName()!=null;
		boolean ods = commonFields.getEventODSCode()!=null;
		boolean org = commonFields.getEventOrganisationName()!=null;
		
		if ((name && ods && org) || (!name && !ods && !org))  {
			// OK
		} else {
			missingFields.addMissingField("eventPerformerName", "If an event performer is provided, the name, ODS code and organisation name must all be included");
		}
		if (commonFields.getEventType() == null) {
			missingFields.addMissingField("eventType", "If an event performer is provided, the type of event must also be included");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		ServiceEvent template = new ServiceEvent();
		
		// ID
		template.setId(CDAUUID.generateUUIDString());
		
		// eventClass
		template.setClassCode(commonFields.getEventType().code);
		
		// eventCode
		template.setEventCode(commonFields.getEventCode());
		
		// eventEffectiveFromTime
		// eventEffectiveToTime
		if (from || to) {
			DateRange r = new DateRange();
			if (from) r.setLow( commonFields.getEventEffectiveFromTime());
			if (to)   r.setHigh(commonFields.getEventEffectiveToTime());
			template.setEffectiveTime(r);
		}
		// eventPerformerName
		if (name) {
			PersonWithOrganizationUniversal performer = new PersonWithOrganizationUniversal();
			
			performer.addPersonId(new PersonID().setNullFlavour(NullFlavour.NI.code));
			
			performer.setPersonName(commonFields.getEventPerformerName());
			
			// eventODSID
			performer.setOrgId(new OrgID()
									.setID(commonFields.getEventODSCode())
									.setType(OrgIDType.ODSOrgID.code));
			// eventOrganisatioName
			performer.setOrgName(commonFields.getEventOrganisationName());
			
			template.addEventPerformer(new ServiceEventPerformer()
												.setPerformer(performer)
												.setPerformerType(HL7PerformerType.Performer.code));
		}
		return template;
	}
	
	public static EncompassingEncounter createEncompassingEncounter(TransferOfCareCommonFields commonFields) throws MissingMandatoryFieldException {
		MissingMandatoryFieldException missingFields = new MissingMandatoryFieldException();
		boolean from = commonFields.getEncounterFromTime()!=null;
		boolean to = commonFields.getEncounterToTime()!=null;
		// Null checks for mandatory fields
		if (!from && !to) {
			missingFields.addMissingField("encounterFromTime", "If an encounter is included, it must have a start and/or end time");
		}
		if (commonFields.getEncounterLocationType() == null) {
			missingFields.addMissingField("encounterLocationType", "If an encounter is included, it must have a location type");
		}
		if (missingFields.hasEntries()) {
			throw missingFields;
		}
		
		EncompassingEncounter template = new EncompassingEncounter();
		// ID
		template.setId(CDAUUID.generateUUIDString());
		// encounterFromTime
		// encounterToTime
		if (from || to) {
			DateRange r = new DateRange();
			if (from) r.setLow( commonFields.getEncounterFromTime());
			if (to)   r.setHigh(commonFields.getEncounterToTime());
			template.setEffectiveTime(r);
		}
		// encounterType
		template.setCode(commonFields.getEncounterType());
		// encounterLocationType
		template.setEncounterCareSettingType(commonFields.getEncounterLocationType());
		// encounterLocationName
		if (commonFields.getEncounterLocationName() != null) {
			template.setEncounterPlaceName(commonFields.getEncounterLocationName());
		}
		// encounterLocationAddress
		if (commonFields.getEncounterLocationAddress() != null) {
			template.setEncounterPlaceAddress(commonFields.getEncounterLocationAddress());
		}
		return template;
	}
}

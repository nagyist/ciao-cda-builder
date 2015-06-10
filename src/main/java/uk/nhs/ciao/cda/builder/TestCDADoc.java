package uk.nhs.ciao.cda.builder;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import uk.nhs.interoperability.payloads.CodedValue;
import uk.nhs.interoperability.payloads.DateValue;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.ConsentID;
import uk.nhs.interoperability.payloads.commontypes.OrgID;
import uk.nhs.interoperability.payloads.commontypes.PatientID;
import uk.nhs.interoperability.payloads.commontypes.PersonID;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.commontypes.RoleID;
import uk.nhs.interoperability.payloads.commontypes.Telecom;
import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;
import uk.nhs.interoperability.payloads.noncodedcdav2.PrimaryRecipient;
import uk.nhs.interoperability.payloads.templates.AuthorPersonUniversal;
import uk.nhs.interoperability.payloads.templates.Consent;
import uk.nhs.interoperability.payloads.templates.CustodianOrganizationUniversal;
import uk.nhs.interoperability.payloads.templates.LanguageCommunication;
import uk.nhs.interoperability.payloads.templates.PatientUniversal;
import uk.nhs.interoperability.payloads.templates.RecipientPersonUniversal;
import uk.nhs.interoperability.payloads.util.CDAUUID;
import uk.nhs.interoperability.payloads.util.FileLoader;
import uk.nhs.interoperability.payloads.vocabularies.generated.DocumentConsentSnCT;
import uk.nhs.interoperability.payloads.vocabularies.generated.Documenttype;
import uk.nhs.interoperability.payloads.vocabularies.generated.HumanLanguage;
import uk.nhs.interoperability.payloads.vocabularies.generated.JobRoleName;
import uk.nhs.interoperability.payloads.vocabularies.generated.Sex;
import uk.nhs.interoperability.payloads.vocabularies.generated.x_BasicConfidentialityKind;
import uk.nhs.interoperability.payloads.vocabularies.internal.AddressType;
import uk.nhs.interoperability.payloads.vocabularies.internal.AttachmentType;
import uk.nhs.interoperability.payloads.vocabularies.internal.DatePrecision;
import uk.nhs.interoperability.payloads.vocabularies.internal.OrgIDType;
import uk.nhs.interoperability.payloads.vocabularies.internal.PatientIDType;
import uk.nhs.interoperability.payloads.vocabularies.internal.PersonIDType;
import uk.nhs.interoperability.payloads.vocabularies.internal.TelecomUseType;


public class TestCDADoc {

	public static void main(String[] args) {
		ClinicalDocument template = createCommonFields();
		// Non XML Body
		template.setNonXMLBodyType(AttachmentType.Base64.code);
		template.setNonXMLBodyMediaType("text/xml");
		String data = FileLoader.loadFile(TestCDADoc.class.getResourceAsStream("/attachment.txt"));
		// Using the standard Java 6 base64 encoder
		String base64data = Base64.encodeBase64String(data.getBytes());
		template.setNonXMLBodyText(base64data);
		
		// Serialise and dump to stdout
		System.out.println(template.serialise());
	}
	
	public static ClinicalDocument createCommonFields() {
		ClinicalDocument template = new ClinicalDocument();
		
		// ==== Set the basic document information ====
		template.setDocumentId(CDAUUID.generateUUIDString());
		template.setDocumentTitle("Report");
		template.setDocumentType(Documenttype._Report);
		template.setEffectiveTime(new DateValue(new Date(), DatePrecision.Minutes));
		template.setConfidentialityCode(x_BasicConfidentialityKind._V);
		template.setDocumentSetId(CDAUUID.generateUUIDString());
		template.setDocumentVersionNumber("1");
		// Patient
		template.setPatient(createPatient()); // From PDS
		// Author
		template.setTimeAuthored(new DateValue(new Date(), DatePrecision.Minutes)); // Take from file metadata?
		template.setAuthor(createAuthor()); // From static JSON?
		// Custodian
		template.setCustodianOrganisation(createCustodian()); // From static JSON?
		// Recipients
		template.addPrimaryRecipients(new PrimaryRecipient(createRecipient())); // From PDS		
		// Authorisation
		template.setAuthorizingConsent(createConsent());
		return template;
	}
	
	public static PatientUniversal createPatient() {
		PatientUniversal template = new PatientUniversal();
		template.addPatientID(new PatientID()
									.setPatientID("K12345")
									.setAssigningOrganisation("V396A:Medway PCT")
									.setPatientIDType(PatientIDType.LocalID.code));
		template.addPatientID(new PatientID()
									.setPatientID("993254128")
									.setPatientIDType(PatientIDType.UnverifiedNHSNumber.code));		
		template.addAddress(new Address()
									.addAddressLine("17, County Court")
									.addAddressLine("Woodtown")
									.addAddressLine("Medway")
									.setPostcode("ME5 FS3")
									.setAddressUse(AddressType.Home.code));
		template.addAddress(new Address()
									.addAddressLine("Hightown Retirement Home")
									.addAddressLine("2, Brancaster Road")
									.addAddressLine("Medway")
									.addAddressLine("Kent")
									.setPostcode("ME5 FL5")
									.setAddressUse(AddressType.PhysicalVisit.code));
		template.addTelephoneNumber(new Telecom()
									.setTelecom("tel:01634775667")
									.setTelecomType(TelecomUseType.HomeAddress.code));
		template.addTelephoneNumber(new Telecom()
									.setTelecom("tel:01634451628")
									.setTelecomType(TelecomUseType.VacationHome.code));
		template.addTelephoneNumber(new Telecom()
									.setTelecom("mailto:mark.smith@emailfree.co.uk")
									.setTelecomType(TelecomUseType.HomeAddress.code));
		template.addPatientName(new PersonName()
									.setTitle("Mr")
									.addGivenName("Mark")
									.setFamilyName("Smith"));
		template.setSex(Sex._Male);
		template.setBirthTime(new DateValue("19490101"));
		// Language
		LanguageCommunication language = new LanguageCommunication();
		language.setLanguage(HumanLanguage._en.code);
		template.addLanguages(language);
		// Organisation - Registered GP:
		template.setRegisteredGPOrgId(new OrgID()
									.setID("V396F")
									.setType(OrgIDType.ODSOrgID.code));
		template.setRegisteredGPOrgName("Medway Medical Practice");
		template.addRegisteredGPTelephone(new Telecom()
									.setTelecom("tel:01634111222")
									.setTelecomType(TelecomUseType.WorkPlace.code));
		template.setRegisteredGPAddress(new Address()
									.addAddressLine("Springer Street")
									.addAddressLine("Medway")
									.setPostcode("ME5 5TY")
									.setAddressUse(AddressType.WorkPlace.code));
		return template;
	}
	
	public static AuthorPersonUniversal createAuthor() {
		AuthorPersonUniversal template = new AuthorPersonUniversal();
 		template.addId(new PersonID()
 						.setType(PersonIDType.LocalPersonID.code)
 						.setID("101")
 						.setAssigningOrganisation("5L399:Medway NHS Foundation Trust"));		
 		// In the sample XML the SDSJobRoleName vocab is used (which is an empty vocab). We will use it's OID here:
 		template.setJobRoleName(new CodedValue("OOH02","Nurse Practitioner","2.16.840.1.113883.2.1.3.2.4.17.196"));
 		template.setName(new PersonName()
						.addGivenName("Mary")
						.setFamilyName("Jones"));
		template.setOrganisationId(new OrgID()
									.setID("5L399")
									.setType(OrgIDType.ODSOrgID.code));
		template.setOrganisationName("Medway NHS Foundation Trust");
		return template;
	}
	
	public static CustodianOrganizationUniversal createCustodian() {
		CustodianOrganizationUniversal template = new CustodianOrganizationUniversal();
		template.setId(new OrgID(OrgIDType.ODSOrgID.code, "5L3"));
		template.setName("Medway NHS Foundation Trust");
		return template;
	}
	
	public static RecipientPersonUniversal createRecipient() {
		RecipientPersonUniversal template = new RecipientPersonUniversal();
		template.addId(new RoleID()
							.setID("1234512345")
							.setAssigningOrganisation("V396A:Medway PCT"));
		template.addTelephoneNumber(new Telecom()
											.setTelecom("mailto:t.hall@emailfree.co.uk"));
		template.setJobRoleName(JobRoleName._SpecialistNursePractitioner);
		template.setName(new PersonName()
								.setTitle("Mr")
								.setFamilyName("Hall")
								.addGivenName("Terence"));
		template.setOrgId(new OrgID()
								.setID("V396A")
								.setType(OrgIDType.ODSOrgID.code));
		template.setOrgName("Medway PCT");
		return template;
	}
	
	public static Consent createConsent() {
		Consent template = new Consent();
		template.addID(new ConsentID(CDAUUID.generateUUIDString()));
		template.setConsentCode(DocumentConsentSnCT._Consentgiventosharepatientdatawithspecifiedthirdparty);
		return template;
	}
}

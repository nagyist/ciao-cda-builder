# Transfer Of Care (JSON)

*Description of the JSON format used to build Transfer Of Care clinical documents*

## Introduction

Internally `ciao-cda-builder` uses the `itk-payloads` library to build XML CDA documents. The resulting CDA documents tend to be quite complex with many nested XML elements.

`itk-payloads` simplifies the structure by providing builders which collapse many of the complex nested XML elements into a single object. `ciao-cda-builder` further simplifies the structure by supporting *shortcut/flattened* objects for collections which only contain one object.

For example this (partial) JSON structure:
```json
{
	"properties": {
		"recipients": [
			{
				"name":
				{
					"title": "Mr",
					"fullName": "Peter Example"
				},
				"odsCode": "example-code"
			}
		]
	}
}
```
can be flattened to:
```json
{
	"properties": {
		"recipientNameTitle": "Mr",
		"recipientNameFullName": "Peter Example",
		"recipientODSCode": "example-code"
	}
}
```

> `ciao-cda-builder` uses **case-insensitive** property names for the incoming JSON representation. In the previous example `recipientodsCode` would also have been valid. However, other CIPs in the process *may* require a specific case to be used for some properties.

## Example

The following example uses the [parsed document](https://github.com/nhs-ciao/ciao-docs-parser/blob/master/docs/parsed-document.md) JSON format:
- `originalDocument` is used to specifiy the original source document content (if applicable)
- `properties` contains the properties used to build the CDA clinical document
- `properties.itkHandlingSpec` defines the *type* of CDA clinical document to build (in this case Transfer Of Care)
- `properties.attachOriginalDocument` specifies whether the `originalDocument` content should be included in the generated CDA clinical document or not

```json
{
	"properties": {
		"Prescriber": "Sarah Phillips",
		"Contact Details": "546",
		"Screened by": "John Adams on 2015-06-09 14:42:00.000.",
		"documentAuthorWorkgroupName": "Medical Ward 1",
		"documentAuthorFullName": "Jack Barker",
		"patientNHSNo": "None",
		"patientFullName": "Mary Edn-Training",
		"patientBirthDate": "19560905",
		"patientAddressFull": "36 Muirkirk Road, Catford LONDON, SE6 1BU",
		"clinicalSummary": "We treated her as sepsis. CXR showed ? lesion in right upper lobe and also showed patchy shadows. We arranged for CT CAP on view of loss of weight and lesions in right upper lobe. CT CAP showed achlasia hernia and no evidence of malignancy. We treated her with antibiotics for urinary tract infection. She was stable during the ward stay but she was not calm and screaming most of the time, but not aggressive and harmful to anyone on ward. However, she became a lot calmer with Quetiapine. She was discharged with Quetiapine low dose.",
		"medicationsPharmacistScreeningAuthorFullName": "John Adams",
		"medicationsPharmacistScreeningDate": "20150609144200.000",
		"medicationsPharmacistScreeningAuthorTelephone": "546",
		"admissionDetails": "html encoded details...",
		"plan": "html encoded details...",
		"diagnoses": "html encoded details...",
		"procedures": "html encoded details...",
		"investigations": "html encoded details...",
		"allergies": "html encoded details...",
		"usualGPOrgName": "The Jenner Practice",
		"usualGPAddressLine": [
			"Jenner Health Centre",
			"201 Stanstead Road, Forest Hill",
			"London"
		],
		"usualGPAddressPostcode": "SE23 1HU",

		"documentTitle": "Discharge Summary",
		"careSetting": "Urology",
		"custodianODSCode": "RJZ",
		"custodianOrganisationName": "KING'S COLLEGE HOSPITAL NHS FOUNDATION TRUST",
		"patientLocalIDAssigningAuthority": "RJZ:KING'S COLLEGE HOSPITAL NHS FOUNDATION TRUST",
		"patientNHSNoIsTraced": true,
		
		"documentAuthorOrganisationODSID": "RJZ",
		"documentAuthorOrganisationName": "KING'S COLLEGE HOSPITAL NHS FOUNDATION TRUST",
		"medicationsPharmacistScreeningAuthorOrgName": "KING'S COLLEGE HOSPITAL NHS FOUNDATION TRUST",
		"medicationsPharmacistScreeningAuthorODSCode": "RJZ",
		  
		"patientGender": "female",
		"recipientFullName": "The Jenner Practice",
		"recipientOrganisationName": "The Jenner Practice",
		"recipientODSCode": "1234",
		"recipientAddressLine": [
		  "Jenner Health Centre",
		  "201 Stanstead Road, Forest Hill",
		  "London"
		],
		"recipientAddressPostcode": "SE23 1HU",
		
		"attachOriginalDocument": false,
		"receiverODSCode": "BBB",
		"itkHandlingSpec": "urn:nhs-itk:interaction:primaryRecipienteDischargeInpatientDischargeSummaryDocument-v1-0"
	},

	"originalDocument": {
	    "name": "hello.txt",
	    "content": "SGVsbG8gV29ybGQh",
	    "mediaType": "text/plain"
	  }
}
```

## JSON Mappings
`ciao-cda-builder` uses [Jackson](https://github.com/FasterXML/jackson) to handle the mapping of JSON to Java objects. The core class for the supported mappings is:  [CDABuilderModule](../src/main/java/uk/nhs/ciao/cda/builder/json/CDABuilderModule.java).

### *Shortcut* collections mappings

| Shortcut Singleton Mapping | Full Collection Mapping |
| ---------------- | ----------------------- |
| `properties.recipient${property}` | `properties.recipients[0].${property}` |
| `properties.copyRecipient${property}` | `properties.copyRecipients[0].${property}` |
| `properties.participant${property}` | `properties.participants[0].${property}` |

### Address

| Property Name | JSON Type |
| ------------- | --------- |
| `line` | String[] |
| `city` | String |
| `postcode` | String |
| `key` | String |
| `full` | String |
| `use` | String |
| `nullFlavour` | String |
| `description` | String |
| `useablePeriod` | DateRange |

### Person Name

| Property Name | JSON Type |
| ------------- | --------- |
| `givenName` | String[] |
| `familyName` | String |
| `fullName` | String |
| `nameType` | String |
| `nullFlavour` | String |

### Date String

Date strings are encoded using supported HL7 formats: from year precision `yyyy`, through to millisecond with timezone precision `yyyyMMddHHmmss.SZ`.

### Date Range

| Property Name | JSON Type |
| ------------- | --------- |
| `from` *or* `low` | Date String |
| `to` *or* `high` | Date String |
| `on` *or* `center` | Date String |

### Document Recipient

| Property Name | JSON Type |
| ------------- | --------- |
| `name` | *unwrapped* Person Name |
| `address` | Address |
| `telephone` | String |
| `jobRole` | Enum String |
| `odsCode` | String |
| `organisationName` | String |
| `sdsId` | String |
| `sdsRoleId` | String |

### Document Participant

| Property Name | JSON Type |
| ------------- | --------- |
| `name` | *unwrapped* Person Name |
| `address` | Address |
| `telephone` | String |
| `odsCode` | String |
| `organisationName` | String |
| `sdsId` | String |
| `sdsRoleId` | String |
| `type` | Enum String |
| `roleClass` | Enum String |


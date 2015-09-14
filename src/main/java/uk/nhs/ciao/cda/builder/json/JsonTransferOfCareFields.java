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

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Lists;

import uk.nhs.interoperability.payloads.helpers.CDADocumentParticipant;
import uk.nhs.interoperability.payloads.helpers.DocumentRecipient;
import uk.nhs.interoperability.payloads.helpers.TransferOfCareFields;
import uk.nhs.interoperability.payloads.util.Emptiable;

import static uk.nhs.interoperability.payloads.util.Emptiables.*;

/**
 * This is a class to help simplify the creation of the non-coded CDA Document, and hide some of
 * the complexity of the underlying document. Once created using this helper, the document
 * can still be fine-tuned using the methods in objects created.
 * 
 * @author Adam Hatherly
 *
 */
public class JsonTransferOfCareFields extends TransferOfCareFields {
	// Special-case properties to support 'single-entry collections' shortcuts in Jackson
	private DocumentRecipient recipient;
	private DocumentRecipient copyRecipient;
	private CDADocumentParticipant participant;
	
	public JsonTransferOfCareFields() {
		super.setRecipients(Lists.<DocumentRecipient>newArrayList());
		super.setCopyRecipients(Lists.<DocumentRecipient>newArrayList());
		super.setParticipants(Lists.<CDADocumentParticipant>newArrayList());
	}
	
	@Override
	public void setRecipients(ArrayList<DocumentRecipient> recipients) {
		getRecipients().clear();
		if (recipients != null) {
			for (final DocumentRecipient recipient: recipients) {
				addRecipient(recipient);
			}
		}
	}
	
	@Override
	public void addRecipient(DocumentRecipient recipient) {
		if (recipient != null) {
			getRecipients().add(recipient);
		}
	}
	
	@Override
	public void setCopyRecipients(ArrayList<DocumentRecipient> copyRecipients) {
		getCopyRecipients().clear();
		if (copyRecipients != null) {
			for (final DocumentRecipient copyRecipient: copyRecipients) {
				addCopyRecipient(copyRecipient);
			}
		}
	}
	
	@Override
	public void addCopyRecipient(DocumentRecipient copyRecipient) {
		if (copyRecipient != null) {
			getCopyRecipients().add(copyRecipient);
		}
	}
	
	@Override
	public void setParticipants(ArrayList<CDADocumentParticipant> participants) {
		getParticipants().clear();
		if (participants != null) {
			for (final CDADocumentParticipant participant: participants) {
				addParticipant(participant);
			}
		}
	}
	
	@Override
	public void addParticipant(CDADocumentParticipant participant) {
		if (participant != null) {
			getParticipants().add(participant);
		}
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
			getRecipients().remove(this.recipient);
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
			getCopyRecipients().remove(this.copyRecipient);
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
	public void setParticipant(final CDADocumentParticipant participant) {
		// clear-old entry
		if (this.participant != null) {
			getParticipants().remove(this.participant);
		}
		this.participant = participant;
		addParticipant(participant);
	}	
	
	/**
	 * Normalises the fields by removing non-null but empty components
	 * 
	 * @see Emptiable
	 */
	public void normalise() {		
		// Remove 'single-entry' shortcuts - these are already handled in the main collections
		recipient = null;
		copyRecipient = null;
		participant = null;
		
		Normalizer.RECIPIENT.normalize(getRecipients());
		Normalizer.RECIPIENT.normalize(getCopyRecipients());
		Normalizer.PARTICIPANT.normalize(getParticipants());
		
		setDocumentEffectiveTime(emptyToNull(getDocumentEffectiveTime()));
		setPatientName(emptyToNull(getPatientName()));
		setPatientBirthDate(emptyToNull(getPatientBirthDate()));		
		setPatientAddress(emptyToNull(getPatientAddress()));
		setUsualGPAddress(emptyToNull(getUsualGPAddress()));
		setTimeAuthored(emptyToNull(getTimeAuthored()));
		setDocumentAuthorAddress(emptyToNull(getDocumentAuthorAddress()));
		setDocumentAuthorName(emptyToNull(getDocumentAuthorName()));
		setDataEntererName(emptyToNull(getDataEntererName()));
		setAuthenticatorName(emptyToNull(getAuthenticatorName()));
		setAuthenticatedTime(emptyToNull(getAuthenticatedTime()));
		setEncounterFromTime(emptyToNull(getEncounterFromTime()));
		setEncounterToTime(emptyToNull(getEncounterToTime()));
		setEncounterLocationType(emptyToNull(getEncounterLocationType()));
		setEncounterLocationAddress(emptyToNull(getEncounterLocationAddress()));
		setMedicationsPharmacistScreeningAuthorName(emptyToNull(getMedicationsPharmacistScreeningAuthorName()));
		setMedicationsPharmacistScreeningDate(emptyToNull(getMedicationsPharmacistScreeningDate()));
	}
}

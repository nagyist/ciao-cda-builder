package uk.nhs.ciao.cda.builder.json;

import java.util.Iterator;

import uk.nhs.interoperability.payloads.helpers.CDADocumentParticipant;
import uk.nhs.interoperability.payloads.helpers.DocumentRecipient;
import static uk.nhs.interoperability.payloads.util.Emptiables.*;

public abstract class Normalizer<T> {
	public static final DocumentRecipientNormalizer RECIPIENT = new DocumentRecipientNormalizer();
	public static final CDADocumentParticipantNormalizer PARTICIPANT = new CDADocumentParticipantNormalizer();
	
	public abstract boolean isEmpty(final T object);
	public abstract void normalize(final T object);
	
	public void normalize(final Iterable<? extends T> values) {
		if (values == null) {
			return;
		}
		
		final Iterator<? extends T> iterator = values.iterator();
		while (iterator.hasNext()) {
			final T value = iterator.next();
			normalize(value);
			
			if (isEmpty(value)) {
				iterator.remove();
			}
		}
	}
	
	public static class DocumentRecipientNormalizer extends Normalizer<DocumentRecipient> {
		@Override
		public boolean isEmpty(final DocumentRecipient recipient) {
			if (recipient == null) {
				return true;
			}
			
			return isNullOrEmpty(recipient.getRecipientName()) &&
					isNullOrEmpty(recipient.getRecipientAddress()) &&
					isNullOrEmpty(recipient.getRecipientTelephone()) &&
					recipient.getRecipientJobRole() == null &&
					isNullOrEmpty(recipient.getRecipientODSCode()) &&
					isNullOrEmpty(recipient.getRecipientOrganisationName()) &&
					isNullOrEmpty(recipient.getRecipientSDSID()) &&
					isNullOrEmpty(recipient.getRecipientSDSRoleID());
		}
		
		@Override
		public void normalize(final DocumentRecipient recipient) {
			if (recipient == null) {
				return;
			}
			
			recipient.setRecipientName(emptyToNull(recipient.getRecipientName()));
			recipient.setRecipientAddress(emptyToNull(recipient.getRecipientAddress()));
		}
	}
	
	public static class CDADocumentParticipantNormalizer extends Normalizer<CDADocumentParticipant> {
		@Override
		public boolean isEmpty(final CDADocumentParticipant participant) {
			if (participant == null) {
				return true;
			}
			
			return isNullOrEmpty(participant.getParticipantName()) &&
					isNullOrEmpty(participant.getParticipantSDSID()) &&
					isNullOrEmpty(participant.getParticipantSDSRoleID()) &&
					isNullOrEmpty(participant.getParticipantAddress()) &&
					isNullOrEmpty(participant.getParticipantTelephone()) &&
					isNullOrEmpty(participant.getParticipantODSCode()) &&
					isNullOrEmpty(participant.getParticipantOrganisationName()) &&
					participant.getParticipantType() == null &&
					participant.getParticipantRoleClass() == null;
		}
		
		@Override
		public void normalize(final CDADocumentParticipant participant) {
			if (participant == null) {
				return;
			}
			
			participant.setParticipantName(emptyToNull(participant.getParticipantName()));
			participant.setParticipantAddress(emptyToNull(participant.getParticipantAddress()));
		}
	}
}

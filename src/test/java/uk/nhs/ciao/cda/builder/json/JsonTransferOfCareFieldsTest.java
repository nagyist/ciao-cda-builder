package uk.nhs.ciao.cda.builder.json;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import uk.nhs.interoperability.payloads.helpers.CDADocumentParticipant;
import uk.nhs.interoperability.payloads.helpers.DocumentRecipient;
import uk.nhs.interoperability.payloads.util.Emptiable;

import com.google.common.collect.Lists;

/**
 * Unit tests for {@link JsonTransferOfCareFields}
 */
public class JsonTransferOfCareFieldsTest {
	@Test
	public void testNormaliseRemovesEmptyObjects() throws Exception {
		final List<Field> emptiableFields = getEmptiableFields(JsonTransferOfCareFields.class);
		
		for (final Field field: emptiableFields) {
			final JsonTransferOfCareFields object = new JsonTransferOfCareFields();
			field.set(object, createEmptyInstance(field));
			
			// Field should be removed
			object.normalise();
			
			Assert.assertNull(field.getName() + " [class=" + field.getType().getSimpleName()  + "]", field.get(object));
		}
	}
	
	@Test
	public void testDocumentRecipientNormalizerRemovesEmptyObjects() throws Exception {
		testNormalizerRemovesEmptyObjects(DocumentRecipient.class, Normalizer.RECIPIENT);
	}
	
	@Test
	public void testCDADocumentParticipantNormalizerRemovesEmptyObjects() throws Exception {
		testNormalizerRemovesEmptyObjects(CDADocumentParticipant.class, Normalizer.PARTICIPANT);
	}
	
	private <T> void testNormalizerRemovesEmptyObjects(final Class<T> clazz, final Normalizer<T> normalizer) throws Exception {
		final List<Field> emptiableFields = getEmptiableFields(clazz);
		
		for (final Field field: emptiableFields) {
			final T object = clazz.newInstance();
			field.set(object, createEmptyInstance(field));
			
			// Field should be removed
			normalizer.normalize(object);
			
			Assert.assertNull(field.getName() + " [class=" + field.getType().getSimpleName()  + "]", field.get(object));
		}
	}

	private List<Field> getEmptiableFields(final Class<?> initialClass) {
		// Detect all emptiable fields
		final List<Field> emptiableFields = Lists.newArrayList();
		for (Class<?> clazz = initialClass; clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
			for (final Field field: clazz.getDeclaredFields()) {
				if (Emptiable.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					emptiableFields.add(field);
				}
			}
		}
		return emptiableFields;
	}
	
	private Object createEmptyInstance(final Field field) {
		final Emptiable emptiable = (Emptiable)Mockito.mock(field.getType());
		Mockito.when(emptiable.isEmpty()).thenReturn(true);
		return emptiable;
	}
}

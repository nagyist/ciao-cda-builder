package uk.nhs.ciao.cda.builder.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.nhs.ciao.cda.builder.json.JacksonMixins.JsonTransferOfCareFieldsMixin;
import uk.nhs.interoperability.payloads.CodedValue;
import uk.nhs.interoperability.payloads.DateValue;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.DateRange;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.helpers.CDACommonFields;
import uk.nhs.interoperability.payloads.helpers.CDADocumentParticipant;
import uk.nhs.interoperability.payloads.helpers.DocumentRecipient;
import uk.nhs.interoperability.payloads.helpers.TransferOfCareFields;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Maps;

/**
 * Utility class using reflection to generate a markdown JSON property table
 * <p>
 * To bootstrap creation of the markdown docs pages
 */
public class MarkdownPropertyTableGenerator {
	public static void main(final String[] args) throws Exception {
		@SuppressWarnings("unchecked")
		final List<? extends Class<?>> types = Arrays.asList(CodedValue.class, Address.class, PersonName.class,
				DateValue.class, DateRange.class, DocumentRecipient.class, CDADocumentParticipant.class);
		final MarkdownPropertyTableGenerator generator = new MarkdownPropertyTableGenerator(types);
		
		generator.writeTable("Common Document Properties", CDACommonFields.class,  JsonTransferOfCareFieldsMixin.class);
		generator.writeTable("Transfer Of Care Document (extends Common Document Properties)", TransferOfCareFields.class, JsonTransferOfCareFieldsMixin.class);
		generator.writeTable(null, JsonTransferOfCareFields.class, JsonTransferOfCareFieldsMixin.class);
		generator.writeTable(CodedValue.class, CodedValueMixin.class);
	}
	
	private Map<Class<?>, String> typeLinks;
	
	public MarkdownPropertyTableGenerator(final List<? extends Class<?>> types) {
		this.typeLinks = createTypeLinks(types);
	}
	
	private Map<Class<?>, String> createTypeLinks(final List<? extends Class<?>> types) {
		final Map<Class<?>, String> links = Maps.newLinkedHashMap();
		
		for (final Class<?> type: types) {
			final String name = splitCamelCase(type.getSimpleName());
			final String fragment = "#" + name.replaceAll(" ", "-").toLowerCase();
			links.put(type, "[" + name + "](" + fragment + ")");
		}
		
		return links;
	}
	
	public String type(final Class<?> type) {
		String value = typeLinks.get(type);
		if (value == null) {
			value = splitCamelCase(type.getSimpleName());
		}
		return value;
	}
	
	public Map<String, String> extract(final Class<?> clazz, final Class<?> mixin) {
		final Map<String, String> map = Maps.newLinkedHashMap();
		final Map<String, String> unwrappedProperties = findUnwrappedProperties(clazz);
		if (mixin != null) {
			unwrappedProperties.putAll(findUnwrappedProperties(mixin));
		}
		
		for (final Field field: clazz.getDeclaredFields()) {
			field.setAccessible(true);
			
			String name = field.getName();
			String value = type(field.getType());
			if (List.class.isAssignableFrom(field.getType())) {
				final ParameterizedType type = (ParameterizedType)field.getGenericType();
				value = type(((Class<?>)type.getActualTypeArguments()[0])) + "[]";
			} else if (Enum.class.isAssignableFrom(field.getType())) {
				value = "Enum String";
			}
			
			if (unwrappedProperties.containsKey(name)) {
				name = unwrappedProperties.get(name);
				value = "*Unwrapped* " + value;
			}
			
			map.put(name,  value);
		}
		
		return map;
	}	
	
	public Map<String, String> findUnwrappedProperties(final Class<?> clazz) {
		final Map<String, String> unwrappedProperties = Maps.newLinkedHashMap();
		for (final Field field: clazz.getDeclaredFields()) {
			field.setAccessible(true);
			JsonUnwrapped unwrapped = field.getAnnotation(JsonUnwrapped.class);
			if (unwrapped == null) {
				try {
					final Method method = clazz.getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
					unwrapped = method.getAnnotation(JsonUnwrapped.class);
				} catch (final NoSuchMethodException e) {
					
				}
			}
			if (unwrapped != null) {
				unwrappedProperties.put(field.getName(), unwrapped.prefix());
			}
		}
		return unwrappedProperties;
	}
	
	public void writeTable(final Class<?> clazz, final Class<?> mixin) {
		writeTable(splitCamelCase(clazz.getSimpleName()), clazz, mixin);
	}
	
	public void writeTable(final String name, final Class<?> clazz, final Class<?> mixin) {
		if (name != null) {
			System.out.println("### " + name);
			System.out.println();
	
			System.out.println("| Property Name | JSON Type |");
			System.out.println("| ------------- | --------- |");
		}
			
		for (final Entry<String, String> entry: extract(clazz, mixin).entrySet()) {
			System.out.append("| `")
				.append(entry.getKey())
				.append("` | ")
				.append(entry.getValue())
				.append(" |");
			System.out.println();
		}
				
		System.out.println();
	}
	
	public static String splitCamelCase(final String s) {
	   return s.replaceAll(
		      String.format("%s|%s|%s",
		         "(?<=[A-Z])(?=[A-Z][a-z])",
		         "(?<=[^A-Z])(?=[A-Z])",
		         "(?<=[A-Za-z])(?=[^A-Za-z])"
		      ),
		      " "
		   );
	}
}

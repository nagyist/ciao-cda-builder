package uk.nhs.ciao.cda.builder.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.nhs.interoperability.payloads.vocabularies.VocabularyEntry;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.google.common.collect.Maps;

/**
 * Jackson deserializer for {@link VocabularyEntry} enums.
 * <p>
 * The serialized value is {@link VocabularyEntry#getCode()}.
 * <p>
 * On construction, the set of known values is supplied and a code
 * to instance lookup map is compiled. Additional mappings
 * can be added later via {@link #addEntry(String, VocabularyEntry)}
 * 
 * @param <T> The concrete type of entry handled by this deserializer
 */
class VocabularyEntryDeserializer<T extends VocabularyEntry> extends FromStringDeserializer<T> {
	private static final long serialVersionUID = 3689147032997601765L;
	private final HashMap<String, T> entriesByKey;
	private final boolean caseSensitive;
	
	/**
	 * Constructs a new case-sensitive deserializer
	 */
	public VocabularyEntryDeserializer(final Class<T> entryType, final T[] entries) {
		this(entryType, entries, true);
	}
	
	/**
	 * Constructs a new deserializer using the specified case-sensitivity
	 */
	public VocabularyEntryDeserializer(final Class<T> entryType, final T[] entries, final boolean caseSensitive) {
		super(entryType);
		
		this.entriesByKey = Maps.newHashMap();
		this.caseSensitive = caseSensitive;
		
		for (final T entry: entries) {
			addEntry(entry.getCode(), entry);
		}
	}
	
	/**
	 * Adds a new code to instance entry mapping
	 */
	public final void addEntry(final String code, final T entry) {
		final String key = getKey(code);
		entriesByKey.put(key, entry);
	}
	
	/**
	 * Adds the specified code to instance entry mappings
	 */
	public void addEntries(final Map<String, T> entryMappings) {
		for (final Entry<String, T> entry: entryMappings.entrySet()) {
			addEntry(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	protected final T _deserialize(final String code, final DeserializationContext ctxt)
			throws IOException {
		final String key = getKey(code);
		final T entry = entriesByKey.get(key);
		if (entry == null) {
			throw new IllegalArgumentException("Unsupported code: " + code);
		}
		return entry;
	}
	
	/**
	 * Returns the lookup key to use for the specified code
	 */
	private String getKey(final String code) {
		return code == null || caseSensitive ? code : code.toLowerCase();
	}
}

package uk.nhs.ciao.cda.builder;

import java.io.InputStream;

import org.apache.activemq.util.ByteArrayInputStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Represents the binary content of a named document.
 * <p>
 * The content of the document is maintained in memory.
 * <p>
 * When de-serialising instances of the class, Jackson uses the annotated constructor
 * of this class to determine which JSON properties to include.
 */
public class Document {
	private final String name;
	private final byte[] content;
	
	/**
	 * Constructs a new document instance
	 * 
	 * @param name The name of the document
	 * @param content The document content - the byte array is stored directly,
	 * 			no defensive copies are made
	 */
	@JsonCreator
	public Document(@JsonProperty("name") final String name,
			@JsonProperty("content") final byte[] content) {
		this.name = Preconditions.checkNotNull(name);
		this.content = Preconditions.checkNotNull(content);
	}
	
	/**
	 * The name of the document (e.g. file name)
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The content of the document
	 * <p>
	 * Jackson de-serialises this property using Base64 encoding.
	 */
	public byte[] getContent() {
		return content;
	}
	
	/**
	 * The content of the document as an input stream
	 */
	@JsonIgnore
	public InputStream getStream() {
		return new ByteArrayInputStream(content);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("name", name)
				.add("size", content.length)
				.toString();
	}
}
package uk.nhs.ciao.cda.builder.json;

/**
 * Interface for classes which can be normalised
 */
public interface Normalisable {
	/**
	 * Update the object instance to a normalised state
	 */
	void normalise();
}

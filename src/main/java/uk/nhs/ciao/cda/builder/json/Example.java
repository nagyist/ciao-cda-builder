package uk.nhs.ciao.cda.builder.json;

import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Temporary example class to run through the jackson deserializer configuration
 * <p>
 * The /example.json resource is read from the classpath and converted into serialized
 * ITK XML (via the intermediate java helper classes provided by itk-payloads)
 */
public class Example {
	public static void main(final String[] args) throws Exception {
//		Thread.sleep(20000);
		new Example().run();
	}
	
	public void run() throws Exception {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new CDABuilderModule());
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
		mapper.enable(Feature.ALLOW_COMMENTS);
		
		final ObjectReader reader = mapper.reader(CDABuilderDocument.class);
		
		long time = System.nanoTime();
		int times = 10000;
		for (int i = 0; i < times; i++) {
			final CDABuilderDocument document = reader.readValue(Example.class.getResourceAsStream("/example.json"));
			final ClinicalDocument clinicalDocument = document.createClinicalDocument();
			clinicalDocument.serialise();
		}
		long taken = System.nanoTime() - time;
		System.out.println("Taken: " + ((double)taken / (double)(times * 1000000L)));
	}
}

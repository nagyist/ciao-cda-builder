package uk.nhs.ciao.cda.builder.json;

import uk.nhs.interoperability.payloads.noncodedcdav2.ClinicalDocument;

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
		final ObjectMapper mapper = new ObjectMapperConfigurator().createObjectMapper();
		
		final ObjectReader reader = mapper.reader(TransferOfCareDocument.class);
		
		long time = System.nanoTime();
		int times = 1;
		for (int i = 0; i < times; i++) {
			final TransferOfCareDocument document = reader.readValue(Example.class.getResourceAsStream("/partial-example.json"));
			final ClinicalDocument clinicalDocument = document.createClinicalDocument();
			System.out.println(clinicalDocument.serialise());
		}
		long taken = System.nanoTime() - time;
		System.out.println("Taken: " + ((double)taken / (double)(times * 1000000L)));
	}
}

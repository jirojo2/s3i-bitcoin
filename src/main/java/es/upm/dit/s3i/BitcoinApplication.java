package es.upm.dit.s3i;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BitcoinApplication {

	public BitcoinApplication(BitcoinPlayground bitcoinPlayground) {
		bitcoinPlayground.run();
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(BitcoinApplication.class).web(false).run(args);
	}
}

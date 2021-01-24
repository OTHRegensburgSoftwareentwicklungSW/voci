package de.majaf.voci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VociApplication {

	public static void main(String[] args) {
		SpringApplication.run(VociApplication.class, args);
	}

}

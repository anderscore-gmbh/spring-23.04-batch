package de.springbatchdeepdive.hello;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class HelloBootApplication {

	// tag::main[]
	public static void main(String[] args) {
		System.exit(
				SpringApplication.exit(
						SpringApplication.run(HelloBootApplication.class, args)));
	}
	// end::main[]
}

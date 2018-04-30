package com.unibz.hikinghelper;

import com.vaadin.tapio.googlemaps.client.LatLon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner loadData(LocationRepository repository) {
		return (args) -> {
			// save a couple of customers
            repository.deleteAll();
			repository.save(new Location("Trichtingen", new LatLon(48.2751726, 8.6505911)));
            repository.save(new Location("Bonn", new LatLon(50.73743, 7.0982068)));
		/*	repository.save(new Customer("Jack", "Bauer"));
			repository.save(new Customer("Chloe", "O'Brian"));
			repository.save(new Customer("Kim", "Bauer"));
			repository.save(new Customer("David", "Palmer"));
			repository.save(new Customer("Michelle", "Dessler"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			for (Customer bauer : repository
					.findByLastName("Bauer")) {
				log.info(bauer.toString());
			}
			log.info("");*/
		};
	}

}

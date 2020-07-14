package com;

import com.model.Billionaire;
import com.repository.BillionairesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class AllInOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllInOneApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(BillionairesRepository repository) {
		return (args) -> {
			Billionaire billionaire = Billionaire.builder().
					career("dance")
					.firstName("firstName")
					.lastName("lastName")
					.build();

			repository.save(billionaire);

			// fetch all billionaires
			log.info("billionaires found with findAll():");
			log.info("-------------------------------");
			for (Billionaire b : repository.findAll()) {
				log.info(b.toString());
			}
		};
	}

}

package com.example.ticketing_system_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class TicketingSystemProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketingSystemProjectApplication.class, args);
	}

}

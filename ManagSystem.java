package com.ManagSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={"com.ManagSystem.controller"})
@EntityScan("com.ManagSystem.entity")
@EnableJpaRepositories("com.ManagSystem.repository")
public class ManagSystem {

	public static void main(String[] args) {
		SpringApplication.run(ManagSystem.class, args);
	}

}
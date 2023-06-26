package com.dh.keycloakrunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeycloakRunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakRunnerApplication.class, args);
	}

	@Autowired
	KeycloakInitializerRunner keycloakInitializerRunner;

	public void run() {
		keycloakInitializerRunner.run();
	}


}

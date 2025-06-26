package com.lreas.file_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication (exclude = SecurityAutoConfiguration.class)
@EnableMongoRepositories(basePackages = "com.lreas.file_management.repositories.mongo")
@EnableJpaRepositories(basePackages = "com.lreas.file_management.repositories.jpa")
public class FileManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagementApplication.class, args);
	}

}

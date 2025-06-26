package com.lreas.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class DatabaseApplication {
	public static void main(String[] args) {
		SpringApplication.run(DatabaseApplication.class, args);
	}
}

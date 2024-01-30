package com.example.pocjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class PocJwtApplication {
    public static void main(String[] args) {
        SpringApplication.run(PocJwtApplication.class, args);
    }
}

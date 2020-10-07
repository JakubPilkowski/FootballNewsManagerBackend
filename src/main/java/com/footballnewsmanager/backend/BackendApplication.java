package com.footballnewsmanager.backend;

import com.footballnewsmanager.backend.models.Role;
import com.footballnewsmanager.backend.models.RoleName;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


}

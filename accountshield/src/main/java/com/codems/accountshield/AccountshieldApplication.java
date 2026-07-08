package com.codems.accountshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class AccountshieldApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountshieldApplication.class, args);
    }

}

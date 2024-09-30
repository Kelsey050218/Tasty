package com.cs183.tasty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@Slf4j
public class TastyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TastyApplication.class, args);
        log.info("server started");
//        ConfigurableApplicationContext run = SpringApplication.run(TastyApplication.class);
//        System.out.println("111");
    }

}
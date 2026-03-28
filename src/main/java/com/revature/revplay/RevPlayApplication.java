
package com.revature.revplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class RevPlayApplication {

    public static void main(String[] args) {
        log.info("Starting RevPlay application...");
        SpringApplication.run(RevPlayApplication.class, args);
        log.info("RevPlay application started successfully.");
    }

}
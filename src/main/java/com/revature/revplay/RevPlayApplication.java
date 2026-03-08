package com.revature.revplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the primary entry point and heart of the RevPlay application.
 * By using the @SpringBootApplication annotation, it triggers three key
 * features:
 * 1. Component Scanning: It looks through all folders to find controllers,
 * services, and repositories.
 * 2. Auto-Configuration: It automatically sets up the environment based on the
 * libraries we've included (like DNA/Hibernate).
 * 3. Embedded Server: It launches an internal Tomcat server so the app can run
 * without external setup.
 * Think of this class as the "Ignition Switch" that brings the entire music
 * streaming ecosystem to life.
 */
@SpringBootApplication
public class RevPlayApplication {

    /**
     * This is the standard Java main method that the operating system calls to
     * start the program.
     * 
     * Within this method, the following events occur:
     * 1. It delegates the startup process to Spring's SpringApplication utility
     * class.
     * 2. It passes the RevPlayApplication class itself as a blueprint for the
     * application context.
     * 3. It accepts command-line arguments that might be used to configure the app
     * at runtime.
     * 4. It initializes the logging system and displays the Spring Boot banner in
     * the console.
     * 5. Once finished, the application is fully live and ready to accept web
     * traffic on port 8080.
     */
    public static void main(String[] args) {
        SpringApplication.run(RevPlayApplication.class, args);
    }

}
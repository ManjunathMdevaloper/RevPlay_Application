package com.revature.revplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.log4j.Log4j2;

/**
 * This class is the central security configuration for the RevPlay application.
 * It uses Spring Security to define how users are authenticated and which parts
 * of the application are accessible to different types of users (Roles).
 * By annotating this with @EnableWebSecurity and @Configuration, we tell Spring
 * to override its default security settings and use the custom rules defined
 * here.
 * This file acts as a protective shield around our application, ensuring that
 * sensitive data and artist-only tools are properly guarded.
 */
@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

        /**
         * This method defines the password encryption algorithm used throughout the
         * system.
         * 
         * Important aspects of this method include:
         * 1. It creates a 'Bean' so Spring can inject this encoder wherever passwords
         * need processing.
         * 2. It uses BCrypt, which is a strong, industry-standard 'hashing' function.
         * 3. Hashing is a one-way process; we never store plain-text passwords in the
         * database.
         * 4. When a user logs in, their input is hashed and compared to the hash in our
         * records.
         * 5. This protects user accounts even if the database were somehow compromised.
         */
        @Bean
        public static PasswordEncoder passwordEncoder() {
                log.info("Initializing BCryptPasswordEncoder for secure password management");
                // Return a new instance of BCryptPasswordEncoder for secure password management
                return new BCryptPasswordEncoder();
        }

        /**
         * This is the core configuration method that builds the security filter chain.
         * It defines precisely which URLs are public and which require a login.
         * 
         * The configuration defines the following security logic:
         * 1. CSRF Protection: Temporarily disabled to simplify development and API
         * interactions.
         * 2. Public Access: Essential pages like Login, Register, and static assets
         * (CSS/JS) are open to all.
         * 3. Role-Based Access: Specific folders like '/artist/' are strictly locked to
         * users with the 'ARTIST' role.
         * 4. Authenticated Request: Any page not explicitly made public requires a
         * valid user session.
         * 5. Custom Login: We define our own login page and where to go after a
         * successful entry.
         * 6. Logout Handling: A standardized way for users to end their secure
         * sessions.
         * 7. Error Handling: Redirecting users to a custom 403 Access Denied page if
         * they lack permission.
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                log.info("Configuring HttpSecurity filter chain for incoming requests");
                // Line below: Disable Cross-Site Request Forgery protection for easier
                // integration
                http.csrf(csrf -> csrf.disable())
                                // Line below: Start the definition of authorization rules for HTTP requests
                                .authorizeHttpRequests(auth -> auth
                                                // Line below: List all URL patterns that anyone can access without
                                                // logging in
                                                .requestMatchers("/register", "/register/**", "/login", "/login/**",
                                                                "/forgot-password", "/forgot-password/**",
                                                                "/verify-security-question", "/reset-password",
                                                                "/css/**", "/js/**",
                                                                "/images/**", "/uploads/**", "/favicon.ico",
                                                                "/api/media/**", "/library/like/**", "/library/api/**")
                                                // Line below: Grant permission to everyone for the paths listed above
                                                .permitAll()
                                                // Line below: Ensure that paths starting with /user/ require either
                                                // USER or ARTIST roles
                                                .requestMatchers("/user/**").hasAnyRole("USER", "ARTIST")
                                                // Line below: Restrict the artist dashboard and related tools only to
                                                // verified ARTISTs
                                                .requestMatchers("/artist/**").hasRole("ARTIST")
                                                // Line below: Enforce that every other request in the app must be from
                                                // a logged-in user
                                                .anyRequest().authenticated())
                                // Line below: Configure the behavior of the built-in login form
                                .formLogin(form -> form
                                                // Line below: Specify our custom login page URL instead of the default
                                                // one
                                                .loginPage("/login")
                                                // Line below: define the target URL where the login form data is sent
                                                .loginProcessingUrl("/login")
                                                // Line below: Where to send the user after successful login (Home page)
                                                .defaultSuccessUrl("/", true)
                                                // Line below: Where to send the user if they provide wrong credentials
                                                .failureUrl("/login?error=true")
                                                // Line below: Allow everyone to access the login-related pages
                                                .permitAll())
                                // Line below: Configure how the application handles user logouts
                                .logout(logout -> logout
                                                // Line below: Define the URL that triggers a logout operation
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                // Line below: Allow all users to log out at any time
                                                .permitAll())
                                // Line below: Setup handles for security-related errors (like unauthorized
                                // access)
                                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

                // Line below: Finalize and return the security filter chain configuration
                return http.build();
        }

        /**
         * This method provides a bean for the AuthenticationManager.
         * 
         * The AuthenticationManager is the heart of the login process, responsible for:
         * 1. Receiving user credentials when they try to sign in.
         * 2. Validating those credentials against the database using our service layer.
         * 3. Managing the creation and storage of the Security Context (the 'session').
         * 4. It's used by Spring behind the scenes to process the '.formLogin()'
         * configuration.
         * 5. We retrieve it from the standard AuthenticationConfiguration provided by
         * Spring.
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                log.info("Extracting AuthenticationManager from AuthenticationConfiguration");
                // Line below: Extract and return the core authentication manager from the
                // system config
                return configuration.getAuthenticationManager();
        }
}
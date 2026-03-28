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

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
                log.info("Initializing BCryptPasswordEncoder for secure password management");      
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                log.info("Configuring HttpSecurity filter chain for incoming requests");
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/register/**", "/login", "/login/**",
                                "/forgot-password", "/forgot-password/**",
                                "/verify-security-question", "/reset-password",
                                "/css/**", "/js/**",
                                "/images/**", "/uploads/**", "/favicon.ico",
                                "/api/media/**", "/library/like/**", "/library/api/**")
                        .permitAll()
                        .requestMatchers("/user/**").hasAnyRole("USER", "ARTIST")
                        .requestMatchers("/artist/**").hasRole("ARTIST")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                log.info("Extracting AuthenticationManager from AuthenticationConfiguration");
        return configuration.getAuthenticationManager();
    }
}

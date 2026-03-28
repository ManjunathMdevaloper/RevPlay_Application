package com.revature.revplay.service.impl;

import com.revature.revplay.dto.UserRegistrationDto;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthServiceImpl(UserRepository userRepository, ArtistProfileRepository artistProfileRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user: {} (isArtist: {})", registrationDto.getUsername(), registrationDto.isArtist());
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            log.warn("Registration failed: Username {} already exists", registrationDto.getUsername());
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            log.warn("Registration failed: Email {} already exists", registrationDto.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setSecurityQuestion(registrationDto.getSecurityQuestion());
        String securityAnswer = registrationDto.getSecurityAnswer();

        if (securityAnswer == null || securityAnswer.isBlank()) {
            log.warn("Registration failed: Security answer is missing");
            throw new RuntimeException("Security answer is required");
        }

        user.setSecurityAnswer(securityAnswer.toLowerCase()); 
        user.setSecurityHint(registrationDto.getSecurityHint());
        user.setRole(registrationDto.isArtist() ? Role.ARTIST : Role.USER);
        user.setDisplayName(registrationDto.getUsername());

        User savedUser = userRepository.save(user);
        log.info("User {} successfully saved with ID: {}", savedUser.getUsername(), savedUser.getId());

        if (registrationDto.isArtist()) {
            ArtistProfile profile = new ArtistProfile();
            profile.setUser(savedUser);
            profile.setArtistName(savedUser.getUsername());
            artistProfileRepository.save(profile);
            log.info("Artist profile created for user {}", savedUser.getUsername());
        }

        return savedUser;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}

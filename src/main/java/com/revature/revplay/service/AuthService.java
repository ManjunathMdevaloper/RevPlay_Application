package com.revature.revplay.service;

import com.revature.revplay.dto.UserRegistrationDto;
import com.revature.revplay.entity.User;
public interface AuthService {
    User registerUser(UserRegistrationDto registrationDto);

    User findByUsername(String username);

    User findByEmail(String email);
}

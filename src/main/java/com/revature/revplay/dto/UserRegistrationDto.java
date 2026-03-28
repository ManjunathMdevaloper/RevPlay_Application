package com.revature.revplay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class UserRegistrationDto {
    
    @NotEmpty(message = "Username should not be empty")
    private String username;

    
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;

    
    @NotEmpty(message = "Password should not be empty")
    private String password;

    
    private boolean isArtist;

    @NotEmpty(message = "Security question should not be empty")
    private String securityQuestion;

    @NotEmpty(message = "Security answer should not be empty")
    private String securityAnswer;

    private String securityHint;
}
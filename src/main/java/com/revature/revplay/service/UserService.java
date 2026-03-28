package com.revature.revplay.service;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.entity.User;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    
    User getUserByUsername(String username);
    User getUserByUsernameOrEmail(String identifier);

    
    UserDto getUserProfile(String username);

    
    void updateUserProfile(String username, UserDto userDto, MultipartFile profilePic, MultipartFile bannerPic);

    
    boolean verifyUser(String username, String email);

    
    void updatePassword(String username, String newPassword);
}

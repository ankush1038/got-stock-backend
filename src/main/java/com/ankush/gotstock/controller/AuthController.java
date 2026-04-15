package com.ankush.gotstock.controller;

import com.ankush.gotstock.dto.*;
import com.ankush.gotstock.dto.*;
import com.ankush.gotstock.service.EmailService;
import com.ankush.gotstock.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthController {
    @Autowired
     private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO registerDTO)throws MessagingException{
        return userService.registerUser(registerDTO);
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO loginDTO){
        log.info("Inside loginUser");
        return userService.loginUser(loginDTO);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO){
        return userService.resetPassword(resetPasswordDTO);
    }

   @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO)throws MessagingException{
        return userService.forgotPassword(forgotPasswordDTO);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?>resetPassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO ){
        return userService.changePassword(changePasswordDTO );
    }

}


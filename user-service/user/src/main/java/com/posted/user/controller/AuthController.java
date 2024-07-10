package com.posted.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.posted.user.dto.request.LoginRequest;
import com.posted.user.dto.response.LoginResponse;
import com.posted.user.model.User;
import com.posted.user.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    try {
      User registeredUser = authService.registerUser(user);
      return ResponseEntity.ok(registeredUser);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public LoginResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
    return new LoginResponse(authService.login(loginRequest));
  }

}

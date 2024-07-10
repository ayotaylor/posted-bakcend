package com.posted.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.posted.exception.ApiRequestException;
import com.posted.user.config.CustomUserDetailsService;
import com.posted.user.dto.request.LoginRequest;
import com.posted.user.model.User;
import com.posted.user.repo.UserRepository;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Autowired
  private JwtService jwtService;

  //@Transactional
  public User registerUser(User user/*, HttpServletRequest httpServletRequest */) throws Exception {
    Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
    if (existingUserByUsername.isPresent()) {
        throw new Exception("Username already exists");
    }

    Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
    if (existingUserByEmail.isPresent()) {
        throw new Exception("Email already registered");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public String login(LoginRequest loginRequest) {
    try {
      var username = loginRequest.getUsername();

      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      String token = jwtService.generateToken(userDetails.getUsername());

      return token;
    } catch (AuthenticationException e) {
        throw new ApiRequestException("Invalid login credentials", HttpStatus.NOT_FOUND);
    }
  }
}

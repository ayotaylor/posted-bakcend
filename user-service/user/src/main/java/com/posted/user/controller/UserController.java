package com.posted.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.posted.user.model.User;
import com.posted.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
    private UserService userService;

}

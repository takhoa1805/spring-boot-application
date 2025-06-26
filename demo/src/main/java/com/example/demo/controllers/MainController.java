package com.example.demo.controllers;

import com.example.demo.dataobject.UserDO;
import com.example.demo.services.UserService;
import org.apache.logging.log4j.message.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final UserService userService;
    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
    @PostMapping("/users/new")
    public UserDO newUser(@RequestBody UserDO user){
        return userService.createUser(user);
    }
    @GetMapping("/users")
    public List<UserDO> getUsers() {
        logger.info("Hello world from get all user api");
        return userService.getUsers();
    }
    @GetMapping("/users/{username}")
    public UserDO getUserByUsername(@PathVariable String username) {
        logger.info("Hello world from get user by username api");
        logger.debug("username: {}", username);
        UserDO user = userService.getUserByUsername(username);
        logger.info("User found: {}", user.getUsername());
        return user;
    }
}

package com.example.demo.controllers;

import com.example.demo.dataobject.database2.User2DO;
import com.example.demo.dataobject.database1.UserDO;
import com.example.demo.services.UserService;
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
    @PostMapping("/v1/users")
    public UserDO newUser(@RequestBody UserDO user){
        return userService.createUser(user);
    }
    @GetMapping("/v1/users")
    public List<UserDO> getUsers() {
        logger.info("Hello world from get all user api");
        return userService.getUsers();
    }
    @GetMapping("/v1/users/{username}")
    public UserDO getUserByUsername(@PathVariable String username) {
        logger.info("Hello world from get user by username api");
        logger.debug("username: {}", username);
        UserDO user = userService.getUserByUsername(username);
        logger.info("GithubUser found: {}", user.getUsername());
        return user;
    }

    @PostMapping("/v2/users")
    public User2DO newUser2(@RequestBody User2DO user){
        return userService.createUser2(user);
    }
    @GetMapping("/v2/users")
    public List<User2DO> getUsers2() {
        logger.info("Hello world from get all user api");
        return userService.getUsers2();
    }
    @GetMapping("/v2/users/{username}")
    public UserDO getUser2ByUsername(@PathVariable String username) {
        logger.info("Hello world from get user by username api");
        logger.debug("username: {}", username);
        UserDO user = userService.getUserByUsername(username);
        logger.info("GithubUser found: {}", user.getUsername());
        return user;
    }
}

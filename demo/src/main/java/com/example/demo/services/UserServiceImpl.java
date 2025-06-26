package com.example.demo.services;

import com.example.demo.dataobject.database2.User2DO;
import com.example.demo.dataobject.database1.UserDO;
import com.example.demo.repositories.database2.User2Repository;
import com.example.demo.repositories.database1.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final User2Repository user2Repository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, User2Repository user2Repository) {
        this.userRepository = userRepository;
        this.user2Repository = user2Repository;
    }

    public List<UserDO> getUsers() {
        List<UserDO> users = new ArrayList<UserDO>();
        users = (List<UserDO>) userRepository.findAll();
        return users;
    }

    public UserDO getUserByUsername(String username) {
        UserDO user = userRepository.findByUsername(username);
        return user;
    }

    public UserDO createUser(UserDO user) {
        return userRepository.save(user);
    }

    public List<User2DO> getUsers2() {
        List<User2DO> users = new ArrayList<User2DO>();
        users = (List<User2DO>) user2Repository.findAll();
        return users;
    }

    public User2DO getUser2ByUsername(String username) {
        User2DO user = user2Repository.findByUsername(username);
        return user;
    }

    public User2DO createUser2(User2DO user) {
        return user2Repository.save(user);
    }

}

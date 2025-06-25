package com.example.demo.services;

import com.example.demo.dataobject.UserDO;
import com.example.demo.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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


}

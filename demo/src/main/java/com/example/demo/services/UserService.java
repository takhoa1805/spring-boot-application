package com.example.demo.services;


import com.example.demo.dataobject.UserDO;

import java.util.List;

public interface UserService {
    public List<UserDO> getUsers() ;
    public UserDO getUserByUsername(String username);
    public UserDO createUser(UserDO user);
}

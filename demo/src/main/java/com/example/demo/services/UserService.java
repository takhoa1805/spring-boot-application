package com.example.demo.services;


import com.example.demo.dataobject.database2.User2DO;
import com.example.demo.dataobject.database1.UserDO;

import java.util.List;

public interface UserService {
    public List<UserDO> getUsers() ;
    public UserDO getUserByUsername(String username);
    public UserDO createUser(UserDO user);
    public List<User2DO> getUsers2() ;
    public User2DO getUser2ByUsername(String username);
    public User2DO createUser2(User2DO user);
}

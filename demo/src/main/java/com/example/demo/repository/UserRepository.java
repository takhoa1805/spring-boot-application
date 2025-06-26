package com.example.demo.repository;

import com.example.demo.dataobject.UserDO;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDO,String> {
    public UserDO findByUsername(String username);
    public List<UserDO> findAll();
}

package com.example.demo.repositories.database2;

import com.example.demo.dataobject.database2.User2DO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface User2Repository extends JpaRepository<User2DO,String> {
    public User2DO findByUsername(String username);
    public List<User2DO> findAll();
}

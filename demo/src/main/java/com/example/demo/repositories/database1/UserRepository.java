package com.example.demo.repositories.database1;

import com.example.demo.dataobject.database1.UserDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDO,String> {
    public UserDO findByUsername(String username);
    public List<UserDO> findAll();
}

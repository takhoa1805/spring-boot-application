package com.lreas.authentication.repositories;

import com.lreas.authentication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public List<User> findByUsername(String username);
    public List<User> findByEmail(String email);
    public List<User> findByInvitationCode(String invitationCode);
}

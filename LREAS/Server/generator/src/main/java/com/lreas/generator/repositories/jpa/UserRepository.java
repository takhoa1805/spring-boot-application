package com.lreas.generator.repositories.jpa;

import com.lreas.generator.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByIdAndWorkflowState(String id, User.STATE state);
}

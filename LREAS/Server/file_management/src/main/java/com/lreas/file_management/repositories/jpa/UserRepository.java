package com.lreas.file_management.repositories.jpa;

import com.lreas.file_management.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    @Query(
            value =
                    """
                        select u from users u
                        where u.id = ?1
                    """)
    User findByUserId(String id);
}

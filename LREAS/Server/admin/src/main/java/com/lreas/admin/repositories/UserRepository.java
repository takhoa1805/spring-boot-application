package com.lreas.admin.repositories;

import com.lreas.admin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public List<User> findByUsername(String username);
    public List<User> findByEmail(String email);
    public List<User> findByInvitationCode(String invitationCode);
    public List<User> findByInstitutionName(String institutionName);
    @Query(
            value =
                    """
                        select u from users u
                        where u.id = ?1
                    """)
    User findByUserId(String id);
}

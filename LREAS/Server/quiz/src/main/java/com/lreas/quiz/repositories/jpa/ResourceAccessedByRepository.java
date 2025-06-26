package com.lreas.quiz.repositories.jpa;

import com.lreas.quiz.models.ResourceAccessedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceAccessedByRepository extends JpaRepository<ResourceAccessedBy, String> {
    @Query(
            value = """
                select r from ResourceAccessedBy r
                where r.resource.user.id = ?1
                and r.resource.resource.id = ?2
            """
    )
    ResourceAccessedBy findByUserAndResource(String userId, String resourceId);
}
package com.lreas.generator.repositories.jpa;

import com.lreas.generator.models.ResourceAccessedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query(
        value = """
            select r from ResourceAccessedBy r
            where r.resource.resource.id = ?1
        """
    )
    List<ResourceAccessedBy> findAllByResource(String resourceId);
}